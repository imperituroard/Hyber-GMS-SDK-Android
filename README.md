# Hyber-SVC-SDK-Android

First connect SDK to your project. Instruction is here: https://jitpack.io/private

Then add to your gradle project this information:


#######
build.gradle (:app)

apply plugin: 'com.google.gms.google-services'

dependencies {
    
    implementation 'com.google.firebase:firebase-messaging:20.0.0'
    implementation 'com.google.firebase:firebase-core:17.2.0'
    implementation "android.arch.lifecycle:extensions:1.1.1"
    implementation 'com.github.imperituroard:Hyber-SVC-SDK-Android:v1.0.0'
}
########


########
build.gradle (:project)

########

dependencies {

        classpath 'com.google.gms:google-services:4.2.0'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
}

allprojects {
    repositories {
        maven {
            url 'https://jitpack.io'
            credentials { username authToken }
        }
    }
}

########

########
gradle.properties
########

authToken=jp_su5amhfnpcr95k8lu2nj9tu9ou
########


And than sync your gradle.

Installation completed. Now you can use Hyber SDK

To start firebase service add service in your AndroidManifest.xml file

<service
    android:name="com.sk.android.sksdkandroid.HyberFirebaseService">
    <intent-filter>
         <action android:name="com.google.firebase.MESSAGING_EVENT"/>
    </intent-filter>
</service>


Now you can use SDK procedures

import main SDK class

import com.sk.android.sksdkandroid.HyberSDK


for registration 

val registration_test = HyberSK("79191234567", "password", applicationContext)
registration_test.sk_register_new("ClientApiKey", "Fingerprint")


for get function answers you can use special data types

Data type for registration procedure
import com.sk.android.sksdkandroid.core.HyberFunAnswerRegister



to be continued.....
