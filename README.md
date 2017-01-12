# myschedule


#To start the project, you need install the latest version of Android Studio.

1. After installation, in the project selection window will point to the opening of projects with source control systems.

2. Select GitHub, copy the link to the project.

3. Then open it.

4. Connect device with Android 4.1.1 or above and create emulation.

5. In the end, we run the project from the Studio and begin to interact with the program.

# To start the project in docker-android

To build the image must enter the command:

    docker build --tag alexleviz/myschedule <PROJECT_DIR>
    
In image was organized all depends needed for the project.
To start a container image must enter the command:

    docker run -t -v $(pwd)/project alexleviz/myschedule:latest ./gradlew clean assembleRelease
    
In container project will built and .apk of project will generated.

Docker was run on OS Elementary OS 0.4 Loki

# _Download [Link](https://play.google.com/store/apps/details?id=com.shedule.zyx.myshedule&hl=ru)_
