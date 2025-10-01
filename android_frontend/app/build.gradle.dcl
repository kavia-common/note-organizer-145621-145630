androidApplication {
    namespace = "org.example.app"

    dependencies {
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))
        implementation("androidx.core:core-ktx:1.13.1")
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.drawerlayout:drawerlayout:1.2.0")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.5")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.5")
        implementation("androidx.room:room-runtime:2.6.1")
        implementation("androidx.room:room-ktx:2.6.1")
        implementation("androidx.cardview:cardview:1.0.0")
        implementation("androidx.activity:activity-ktx:1.9.2")
        implementation("androidx.annotation:annotation:1.8.2")
        implementation("androidx.sqlite:sqlite-ktx:2.4.0")
        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
        implementation("androidx.lifecycle:lifecycle-common-java8:2.8.5")
    }
}
