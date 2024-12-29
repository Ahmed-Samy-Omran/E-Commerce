plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")  // for data binding
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id ("kotlin-android")
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
    kaptTest("com.google.dagger:hilt-android-compiler:2.47")
    implementation("com.google.dagger:hilt-android:2.48")
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.4")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    kapt("com.google.dagger:hilt-android-compiler:2.47")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")




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

}

