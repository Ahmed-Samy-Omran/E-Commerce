plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")  // for data binding
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id ("kotlin-android")
    id("com.google.protobuf") version "0.9.4" apply true
}

android {
    namespace = "com.example.e_commerce"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.e_commerce"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            forEach{
                it.buildConfigField(
                    "String",
                    "clientServerId",
                    "\"322843996565-2lg236vhq1i7llpgh92p3lli61k2sde1.apps.googleusercontent.com\""
                )
                it.resValue(
                    "string",
                    "facebook_app_id",
                    "\"1699646247248307\""
                )
                it.resValue(
                    "string",
                    "fb_login_protocol_scheme",
                    "\"fb1699646247248307\""
                )
                it.resValue(
                    "string",
                    "facebook_client_token",
                    "\"b28ef2d76bc60e19224f8a1d9374fdfb\""
                )
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.google.googleid)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation(kotlin("stdlib"))




    // third party libraries
    implementation("com.github.pwittchen:reactivenetwork-rx2:3.0.8")


    // navigation
    val nav_version = "2.8.3"

    // Views/Fragments integration
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Feature module support for Fragments
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")
    val lifecycle_version = "2.8.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    testImplementation("com.google.dagger:hilt-android-testing:2.44.2")
    kaptTest("com.google.dagger:hilt-android-compiler:2.48")
//    implementation("com.google.dagger:hilt-android:2.48")
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.4")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
//    kapt("com.google.dagger:hilt-android-compiler:2.47")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    // firebase libraries

    // that to sign in with google and facebook
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Facebook Android SDK (everything)
//    implementation ("com.facebook.android:facebook-android-sdk:17.0.1")
    implementation("com.facebook.android:facebook-login:17.0.0")


    implementation("com.google.firebase:firebase-crashlytics")



    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-auth:23.1.0")

    implementation ("androidx.credentials:credentials:1.3.0")
    implementation ("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation ("androidx.credentials:credentials:1.2.0-alpha02")
    implementation (libs.credentials.play.services.auth)


    implementation("androidx.datastore:datastore-core:1.1.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.google.protobuf:protobuf-kotlin-lite:4.26.0")
    implementation("androidx.datastore:datastore:1.1.1")


    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    // OkHttp interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.14")

//    implementation("com.google.protobuf:protobuf-javalite:4.26.0") // Java lite runtime
//    implementation("com.google.protobuf:protobuf-kotlin-lite:3.24.0") // Kotlin lite runtime


//    implementation ("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
//    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    

    implementation("androidx.viewpager2:viewpager2:1.1.0")



}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.26.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}
