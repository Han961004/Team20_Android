
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
fun getApiKey(key: String): String = gradleLocalProperties(rootDir, providers).getProperty(key)

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.jetbrains.kotlin.android)
	id("kotlin-kapt")
}

android {
	namespace = "com.example.potatoservice"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.example.potatoservice"
		minSdk = 24
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

		resValue("string", "kakao_api_key", getApiKey("KAKAO_API_KEY"))
		buildConfigField("String", "KAKAO_REST_API_KEY", getApiKey("KAKAO_REST_API_KEY"))
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
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
	buildFeatures {
		viewBinding = true
		dataBinding = true
		viewBinding = true
		buildConfig = true
	}
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.androidx.constraintlayout)
	implementation(libs.androidx.lifecycle.livedata.ktx)
	implementation(libs.androidx.lifecycle.viewmodel.ktx)
	implementation(libs.androidx.navigation.fragment.ktx)
	implementation(libs.androidx.navigation.ui.ktx)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)

	implementation("com.kakao.maps.open:android:2.9.5")
	implementation("com.kakao.sdk:v2-all:2.20.3")

}