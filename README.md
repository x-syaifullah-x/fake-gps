# fake-gps
[![x-syaifullah-x](https://circleci.com/gh/x-syaifullah-x/fake-gps/tree/master.svg?style=svg)](https://circleci.com/gh/x-syaifullah-x/fake-gps/tree/fake-gps-update)
![Build](https://shields.io/github/workflow/status/x-syaifullah-x/fake-gps/build/master?event=push&logo=github&label=Build)
---

## Build
- local.properties
    ```
    debug_store_password=$STORE_PASSWORD
    debug_key_alias=$KEY_ALIAS
    debug_key_password=$KEY_PASSWORD
    debug_store_file=$PATH_JKS

    release_store_password=$STORE_PASSWORD
    release_key_alias=$KEY_ALIAS
    release_key_password=$KEY_PASSWORD
    release_store_file=$PATH_JKS
    ```
- need firebase
    - firebase auth
    - firebase firestore
- add the file google-service.xml in app/src/main/res/values
    ```
    <resources>
        <!-- oauth_client.client_id -->
        <string name="default_web_client_id" templateMergeStrategy="preserve" translatable="false">YOUR DEFAULT WEB CLIENT ID</string>
        <!-- project_info.firebase_url -->
        <string name="firebase_database_url" templateMergeStrategy="preserve" translatable="false">YOUR FIREBASE DATABASE URL</string>
        <!-- project_info.project_number-->
        <string name="gcm_defaultSenderId" templateMergeStrategy="preserve" translatable="false">YOUR GCM DEFAULT SENDER ID</string>
        <!-- api_key[0].current_key-->
        <string name="google_api_key" templateMergeStrategy="preserve" translatable="false">YOUR GOOGLE API KEY</string>
        <!-- client_info.mobilesdk_app_id -->
        <string name="google_app_id" templateMergeStrategy="preserve" translatable="false">YOUR GOOGLE APP ID</string>
        <!-- api_key[1].current_key-->
        <string name="google_crash_reporting_api_key" templateMergeStrategy="preserve" translatable="false">YOUR GOOGLE CRASH REPORTING API KEY</string>
        <!--project_info.storage_bucket-->
        <string name="google_storage_bucket" templateMergeStrategy="preserve" translatable="false">YOUR GOOGLE STORAGE BUCKET</string>
        <!--project_info.project_id-->
        <string name="project_id" templateMergeStrategy="preserve" translatable="false">YOUR PROJECT ID</string>
        <!--api key google map-->
        <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">YOUR GOOGLE MAP API KEY</string>
    </resources>
    ```