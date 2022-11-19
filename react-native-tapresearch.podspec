require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))
folly_compiler_flags = '-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1 -Wno-comma -Wno-shorten-64-to-32'

Pod::Spec.new do |s|
  s.name         = "react-native-tapresearch"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "12.4" }
  s.source       = { :git => "https://github.com/tapresearch/react-native-tapresearch.git", :tag => "v2.5.4" }
#   s.source       = { :git => ""}

  s.source_files = "ios/**/*.{h,m,mm}"

  s.dependency "React"

  s.dependency "React-Core"
  s.dependency "TapResearch", "~> 2.5.4"
end
