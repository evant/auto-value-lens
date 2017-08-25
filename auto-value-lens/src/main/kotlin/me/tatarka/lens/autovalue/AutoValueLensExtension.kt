package me.tatarka.lens.autovalue

import com.gabrielittner.auto.value.util.AutoValueUtil
import com.google.auto.service.AutoService
import com.google.auto.value.extension.AutoValueExtension
import com.squareup.javapoet.*
import javax.annotation.processing.Messager
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic

val lens = ClassName.get("me.tatarka.lens", "Lens")!!
val lensInt = ClassName.get("me.tatarka.lens", "LensInt")!!
val lensLong = ClassName.get("me.tatarka.lens", "LensLong")!!
val lensDouble = ClassName.get("me.tatarka.lens", "LensDouble")!!
val lensClassNames = listOf(lens, lensInt, lensLong, lensDouble)

@AutoService(AutoValueExtension::class)
class AutoValueLensExtension : AutoValueExtension() {

    override fun applicable(context: Context): Boolean {
        val lensClass = findLensClass(context.autoValueClass(), context.processingEnvironment().messager)
        return lensClass != null
    }

    override fun generateClass(context: Context, className: String, classToExtend: String, isFinal: Boolean): String {
        val lensInterface = findLensClass(context.autoValueClass(), context.processingEnvironment().messager)!!
        val lensClassName = ClassName.get(context.packageName(), className, lensInterface.simpleName.toString())
        val lensClassBuilder = TypeSpec.classBuilder(lensInterface.simpleName.toString())
                .addTypeVariables(context.autoValueClass().typeParameters.map { TypeVariableName.get(it) })
                .addModifiers(Modifier.STATIC, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build())
                .addField(FieldSpec.builder(lensClassName, "instance")
                        .addModifiers(Modifier.STATIC, Modifier.FINAL)
                        .initializer("new \$T()", lensClassName)
                        .build())

        if (lensInterface.kind == ElementKind.INTERFACE) {
            lensClassBuilder.addSuperinterface(TypeName.get(lensInterface.asType()))
        } else {
            lensClassBuilder.superclass(TypeName.get(lensInterface.asType()))
        }

        addLensMethods(context, className, lensInterface, lensClassBuilder)
        val lensClass = lensClassBuilder.build()

        val subclass = AutoValueUtil.newTypeSpecBuilder(context, className, classToExtend, isFinal)
                .addType(lensClass)
                .build()

        return JavaFile.builder(context.packageName(), subclass).build().toString()
    }

    private fun findLensClass(autoValueClass: TypeElement, messager: Messager): TypeElement? {
        var foundClass: TypeElement? = null
        for (typeElement in ElementFilter.typesIn(autoValueClass.enclosedElements)) {
            if (typeElement.getAnnotation(AutoValueLenses::class.java) != null) {
                if (foundClass == null) {
                    if (typeElement.modifiers.contains(Modifier.PRIVATE)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "Lens class must not be private")
                        return null
                    }
                    if (!typeElement.modifiers.contains(Modifier.ABSTRACT)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "Lens class must be abstract")
                        return null
                    }
                    foundClass = typeElement
                } else {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Multiple @AutoValueLenses.Lens annotations found", autoValueClass)
                    return null
                }
            }
        }
        return foundClass
    }

    private fun addLensMethods(context: Context, className: String, lensInterface: TypeElement, builder: TypeSpec.Builder) {
        for (element in ElementFilter.methodsIn(lensInterface.enclosedElements)) {
            if (element.modifiers.contains(Modifier.PRIVATE) || !element.modifiers.contains(Modifier.ABSTRACT)) {
                continue
            }
            val returnType = TypeName.get(element.returnType) as? ParameterizedTypeName ?: continue
            if (returnType.rawType !in lensClassNames) {
                continue
            }

            val lensType = LensType(returnType)
            val outerType = TypeName.get(context.autoValueClass().asType())
            val constructorArgs = context.properties().entries
                    .map { if (it.key == element.simpleName.toString()) "inner" else "outer.${it.key}()" }

            val lensClass = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(returnType)
                    .addMethod(MethodSpec.methodBuilder(lensType.getter)
                            .addAnnotation(Override::class.java)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(outerType, "outer")
                            .returns(lensType.innerType)
                            .addStatement("return outer.\$L()", element.simpleName)
                            .build())
                    .addMethod(MethodSpec.methodBuilder(lensType.setter)
                            .addAnnotation(Override::class.java)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(outerType, "outer")
                            .addParameter(lensType.innerType, "inner")
                            .returns(outerType)
                            .addStatement("return new \$T(\$L)", ClassName.get(context.packageName(), className), constructorArgs.joinToString(", "))
                            .build())
                    .build()

            val initializer = CodeBlock.builder()
                    .addStatement("\$L", lensClass)
                    .build()

            builder.addField(FieldSpec.builder(returnType, element.simpleName.toString())
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .initializer(initializer)
                    .build())

            val modifiers: MutableSet<Modifier> = mutableSetOf()
            modifiers.addAll(element.modifiers)
            modifiers.remove(Modifier.ABSTRACT)
            builder.addMethod(MethodSpec.methodBuilder(element.simpleName.toString())
                    .addAnnotation(Override::class.java)
                    .addModifiers(modifiers)
                    .returns(returnType)
                    .addStatement("return \$L", element.simpleName)
                    .build())
        }
    }

    private sealed class LensType {
        class Lens(override val innerType: TypeName) : LensType() {
            override val lensType = lens
            override val getter = "get"
            override val setter = "set"
        }

        object LensInt : LensType() {
            override val lensType = lensInt
            override val innerType = TypeName.INT!!
            override val getter = "getAsInt"
            override val setter = "setAsInt"
        }

        object LensLong : LensType() {
            override val lensType = lensLong
            override val innerType = TypeName.LONG!!
            override val getter = "getAsLong"
            override val setter = "setAsLong"
        }

        object LensDouble : LensType() {
            override val lensType = lensDouble
            override val innerType = TypeName.DOUBLE!!
            override val getter = "getAsDouble"
            override val setter = "setAsDouble"
        }

        abstract val lensType: ClassName
        abstract val innerType: TypeName
        abstract val getter: String
        abstract val setter: String

        companion object {
            operator fun invoke(type: ParameterizedTypeName): LensType = when (type.rawType) {
                lens -> Lens(type.typeArguments[1])
                lensInt -> LensInt
                lensLong -> LensLong
                lensDouble -> LensDouble
                else -> throw IllegalArgumentException("invalid lens type: $type")
            }
        }
    }
}

