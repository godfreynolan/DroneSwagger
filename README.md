# Drone Development with OpenAPI
Credit for Swagger API: [SwaggerHub](https://app.swaggerhub.com/apis/test-theegg/Drone2/1.0.0)

1. Create a new Android Project. Open both the `build.gradle` files.

2. In your app level gradle, add the following dependencies:
    ```gradle=
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'io.reactivex.rxjava3:rxjava:3.0.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.squareup.okhttp3:okhttp:4.10.0'
    implementation 'com.squareup.moshi:moshi:1.14.0'
    ```
3. In your project level gradle, add the following:
    ```gradle=
    import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
    
    plugins {
        ...
        id "org.openapi.generator" version "6.3.0"
        ...
    }

    task generateApi(type: GenerateTask) {
        generatorName = "kotlin"
        inputSpec = "$rootDir/swagger.yaml".toString()
        outputDir = "$buildDir/api-spec/generated".toString()
        apiPackage = "com.riis.droneswagger.api.common"
        invokerPackage = "com.riis.droneswagger.generated.api.invoker.common"
        modelPackage = "com.riis.droneswagger.generated.api.model.common"
        configOptions = [dateLibrary: "java8", useRxJava3:"true", serializationLibrary: "gson"]
    }

    task copyApiFilesIntoProject(type: Copy) {
        dependsOn(generateApi)
        from "$buildDir/api-spec/generated/src/main/kotlin/".toString()
        into "$rootDir/app/src/main/java/".toString()
    }
    ```
    Here we create two tasks, `generateApi` and `copyApiFilesIntoProject`. 
    
    `generateApi` will read in a Swagger API file from the root of your project. Under `configOptions` we just define some options for our future generated code.
    
    In `copyApiFilesIntoProject` we define a task to copy our generated client code to our Android project. This will also run `generateApi` at the same time.
    
4. Next download/copy the Swagger API from the [Github repo](https://github.com/WBawa/DroneSwagger/blob/main/swagger.yaml) or [SwaggerHub](https://app.swaggerhub.com/apis/test-theegg/Drone2/1.0.0). Place the swagger.yaml file in the root directory of this project.
    
5. Now we can run the tasks that we defined earlier. We will be running `copyApiFilesIntoProject` since this will run both our tasks. You can run the task by clicking the run button next to the task declaration.

    ![run task](https://notes.yoh.gay/uploads/d44c34f9-0b6b-4f9b-8249-28bfb8532abc.png)

    In our project view, we should be able to see our generated files, namely the `DroneApi.kt`
    
    ![file generated](https://notes.yoh.gay/uploads/8bd5d28b-1f24-4dda-91d6-5f85e0123c5b.png)

6. Next let's setup our simple layout and main activity. Starting with the layout, open `activity_main.xml` and paste the following inside:
    ```xml=
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:orientation="vertical"
    tools:context=".MainActivity" >
 
                <LinearLayout
                android:layout_width="fill_parent"
                android:layout_height="fill_parent"
                android:layout_weight="1"
                android:orientation="horizontal" >

            <Button
                    android:id="@+id/button1"
                    android:layout_width="fill_parent"
                    android:layout_height="fill_parent"
                    android:layout_weight="1"
                    android:text="1" >
            </Button>

            <Button
                    android:id="@+id/button2"
                    android:layout_width="fill_parent"
                    android:layout_height="fill_parent"
                    android:layout_weight="1"
                    android:text="2" >
            </Button>
        </LinearLayout>

        <LinearLayout
                android:layout_width="fill_parent"
                android:layout_height="fill_parent"
                android:layout_weight="1"
                android:orientation="horizontal" >

            <Button
                    android:id="@+id/button3"
                    android:layout_width="fill_parent"
                    android:layout_height="fill_parent"
                    android:layout_weight="1"
                    android:text="3" >
            </Button>

            <Button
                    android:id="@+id/button4"
                    android:layout_width="fill_parent"
                    android:layout_height="fill_parent"
                    android:layout_weight="1"
                    android:text="4" >
            </Button>
        </LinearLayout>
        <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content">
            <TextView
                    android:id="@+id/resultsView"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:gravity="center"
                    android:text="XXX" />

        </LinearLayout>
    </LinearLayout>

    ``` 
    
    The above will create 4 buttons that we will use to make requests to our server that will be defined later. 

7. In our `MainActivity.kt`, we will insert the following:

    ```kotlin=
    package com.riis.droneswagger

    import android.os.Bundle
    import android.util.Log
    import android.widget.Button
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import com.riis.droneswagger.api.common.DroneApi
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import org.openapitools.client.infrastructure.ApiClient

    class MainActivity : AppCompatActivity() {

        private lateinit var droneApi: DroneApi
        private lateinit var button1: Button
        private lateinit var button2: Button
        private lateinit var button3: Button
        private lateinit var button4: Button
        private lateinit var resultsView: TextView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            button1 = findViewById(R.id.button1)
            button2 = findViewById(R.id.button2)
            button3 = findViewById(R.id.button3)
            button4 = findViewById(R.id.button4)
            resultsView = findViewById(R.id.resultsView)


            // Initialize the API client
            val apiClient = ApiClient("http://insert_local_ip_here:8080/")
            droneApi = DroneApi(apiClient.baseUrl, apiClient.client)

            button1.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val droneId = "123"
                    val droneAction = "takeoff" // or whatever action you want to execute
                    try {
                        val response = droneApi.droneIdActionsPutWithHttpInfo(droneId, droneAction)
                        resultsView.text = response.statusCode.toString()
                    } catch (e: Exception) {
                        Log.e("ERRORS", "Error calling API: ${e.message}")
                    }
                }
            }

            button2.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val droneId = "123"
                    try {
                        val response = droneApi.droneIdGetWithHttpInfo(droneId)
                        resultsView.text = response.statusCode.toString()
                    } catch (e: Exception) {
                        Log.e("ERRORS", "Error calling API: ${e.message}")
                    }
                }
            }

            button3.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val droneId = "123"
                    try {
                        val response = droneApi.droneIdZoomPutWithHttpInfo(droneId)
                        resultsView.text = response.statusCode.toString()
                    } catch (e: Exception) {
                        Log.e("ERRORS", "Error calling API: ${e.message}")
                    }
                }
            }

            button4.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch { val droneId = "123"
                    try {
                        val response = droneApi.droneIdZoomDeleteWithHttpInfo(droneId)
                        resultsView.text = response.statusCode.toString()
                    } catch (e: Exception) {
                        Log.e("ERRORS", "Error calling API: ${e.message}")
                    }
                }
            }

        }
    } 
    ```
    
    > **Pay attention to line 35, insert your local IP of the device that will be running the server.**
    
8. For the last step on the Android side, we will edit our manifest to allow for networking and Clear Text Traffic, edit the manifest like so:
    
    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools" >
        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        <application
            ...
            android:usesCleartextTraffic="true"
            ... >
            <activity
                ...
            </activity>
        </application>
    </manifest>
    ```
    
9. Next we will setup our mock server. We will create a sample flask server to act as our Drone API. As we are just mocking up an example, we will just print out messages on endpoints being activated. Assuming you have Python 3 installed, let's continue.

    Run the following pip commands to install the necessary packages:
    ```
    pip3 install flask jsonify
    # OR
    python3 -m pip install flask jsonify
    ```
    
    Create a file `main.py` and insert the following inside:
    ```python=
    from flask import Flask, jsonify, request

    app = Flask(__name__)

    @app.route('/drone/<string:id>/<string:actions>', methods=['PUT'])
    def send_drone_actions(id, actions):

        print(f"send drone action: {actions}")

        response = {'error': False, 'data': {"id": id, "actions": f"{actions}"}, 'message': 'success'}
        return jsonify(response), 200

    @app.route('/drone/<string:id>', methods=['GET'])
    def get_drone_by_id(id):

        print(f"get drone by id: {id}")

        response = {'error': False, 'data': {"id": id}, 'message': 'success'}
        return jsonify(response), 200

    @app.route('/drone/<string:id>/zoom', methods=['PUT'])
    def zoom_in_drone_by_id(id):

        print(f"zoom in drone by id: {id}")

        response = {'error': False, 'data': {"id": id}, 'message': 'success'}
        return jsonify(response), 200

    @app.route('/drone/<string:id>/zoom', methods=['DELETE'])
    def zoom_out_drone_by_id(id):

        print(f"zoom out drone by id: {id}")

        response = {'error': False, 'data': {"id": id}, 'message': 'success'}
        return jsonify(response), 200

    if __name__ == '__main__':
        app.run(host='0.0.0.0', port=8080)
    ```
    
    To run this server, run `python3 main.py` in another terminal. Take note of which IP addresses this server is running on. Ensure that the local IP matches the IP in line 35 of `MainActivity.kt`.
    
10. Finally, we can run this and give it a test. If everything is working well, we should see our print statements from our flask server.

11. Now we can start the setup of our Github Actions. We can use Github Action workflows to automate some tasks for us. In this case, we will have our action regenerate our Drone API client on each push to our main branch. This way, if we update our Swagger API we can get an automatic build for it.

    For our first step we will grant actions read and write access to our repository. In your repo, navigate to Settings -> Actions -> General and scroll down to Workflow permission. Here we will grant it read/write permissions.
    ![grant workflow perms](https://notes.yoh.gay/uploads/d2e89374-9c05-43eb-bbf8-1822d70b0eaf.png)

12. Next we will setup a Github Action. Navigate to Actions -> set up a workflow yourself. Paste the following in:
    ```yaml=
    name: Generate and Copy API Code

    on:
      push:
        branches: [main]

    jobs:
      build:
        runs-on: ubuntu-latest
        steps:
          - uses: actions/checkout@v2
          - name: Set up JDK 11
            uses: actions/setup-java@v2
            with:
              java-version: 11
              distribution: adopt
          - name: Generate API code
            run: ./gradlew generateApi
          - name: Copy API files
            run: ./gradlew copyApiFilesIntoProject
          - name: Commit and push changes
            uses: stefanzweifel/git-auto-commit-action@v4
            with:
              commit_message: "Automatically update API code"
              commit_options: "--no-verify"
              branch: main
              file_pattern: '**/*'
    ```
    This action will be activated on changes to the main branch. We will run our gradle tasks that we defined earlier. We will then commit any changes to the Drone API to our main branch if there are any differences with the `swagger.yaml`.
    
    You should now be able to commit and push your commit, this should trigger a build with your new Action. 
    
13. To test further, you can edit the `swagger.yaml` in Github, make a change to one of the endpoints and see a build trigger to ensure that you see a new commit be pushed from your Action. 
