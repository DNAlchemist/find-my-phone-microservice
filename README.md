# Find my phone

An application using google service to find your android device. 

## Run application

    // startup application
    ./gradlew run
    
    // get your device list
    curl localhost:5050/android/devices -u "yourgooglemail:yourgooglepassword"
    
## Run tests

    ./gradlew check