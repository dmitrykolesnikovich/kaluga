Pod::Spec.new do |spec|
    spec.name                     = 'firebase'
    spec.version                  = '0.0.5'
    spec.homepage                 = 'https://kaluga.splendo.com'
    spec.source                   = { :git => "Not Published", :tag => "Cocoapods/#{spec.name}/#{spec.version}" }
    spec.authors                  = 'Splendo'
    spec.license                  = 'Apache Licence, Version 2.0'
    spec.summary                  = 'Kaluga'

    spec.static_framework         = true
    spec.vendored_frameworks      = "build/cocoapods/framework/firebase.framework"
    spec.libraries                = "c++"
    spec.module_name              = "#{spec.name}_umbrella"

    spec.dependency 'FirebaseCore', '6.0.2'
    spec.dependency 'FirebaseFirestore', '1.3.2'

    spec.pod_target_xcconfig = {
        'KOTLIN_TARGET[sdk=iphonesimulator*]' => 'ios_x64',
        'KOTLIN_TARGET[sdk=iphoneos*]' => 'ios_arm'
    }

    spec.script_phases = [
        {
            :name => 'Build firebase',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" :firebase:syncFramework \
                    -Pkotlin.native.cocoapods.target=$KOTLIN_TARGET \
                    -Pkotlin.native.cocoapods.configuration=$CONFIGURATION \
                    -Pkotlin.native.cocoapods.cflags="$OTHER_CFLAGS" \
                    -Pkotlin.native.cocoapods.paths.headers="$HEADER_SEARCH_PATHS" \
                    -Pkotlin.native.cocoapods.paths.frameworks="$FRAMEWORK_SEARCH_PATHS"
            SCRIPT
        }
    ]
end
