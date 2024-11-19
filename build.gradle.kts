plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.parkwoocheol.firebase.firestore.ktx"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    api(libs.firebase.firestore)
    implementation(libs.coroutines.android)
}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.parkwoocheol"
                artifactId = "firebase-firestore-ktx"
                version = "1.0.0"

                pom {
                    name.set("Firebase Firestore KTX")
                    description.set("Type-safe Kotlin extensions for Firebase Firestore with coroutines support")
                    url.set("https://github.com/parkwoocheol/firebase-firestore-ktx")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("parkwoocheol")
                            name.set("Woocheol Park")
                        }
                    }
                }
            }
        }
    }
}