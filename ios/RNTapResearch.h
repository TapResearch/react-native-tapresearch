#if __has_include(<React/RCTEventEmitter.h>)
#import <React/RCTEventEmitter.h>
#import <React/RCTBridgeModule.h>
#else
#import "RCTEventEmitter.h"
#import "RCTBridgeModule.h"
#endif

#import <TapResearchSDK/TapResearchSDK.h>

@interface RNTapResearch : RCTEventEmitter <RCTBridgeModule, TapResearchDelegate, TapResearchSurveyDelegate>

@end
