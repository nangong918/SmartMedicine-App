plugins {
    alias(libs.plugins.android.library)
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.czy.dal"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        javaCompileOptions {
            annotationProcessorOptions {
                // 指定 Room Migration 升级数据库导出的 Schema 文件位置
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }

        // 如果使用 Kotlin：（使用了 kapt，不使用这里会报错；用了可以不用上面那行了）
        kapt {
            arguments {
                // 指定 Room Migration 升级数据库导出的 Schema 文件位置
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        // 启用 aidl
        aidl = true
    }
    sourceSets {
        named("main") {
            // Aidl 文件
            java.srcDirs("src/main/java", "src/main/aidl")
        }
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // protobuf
    implementation(libs.protobuf.java)


    // Room
    kapt(libs.androidx.room.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.androidx.room.rxjava2)
    annotationProcessor(libs.androidx.room.room.compiler) // 如果您仍然使用 annotationProcessor

    // 自定义依赖
    implementation(project(":core:BaseUtilsLib"))
}