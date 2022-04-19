#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <TapResearchSDK/TapResearchSDK.h>

#define PLATFORM @"react"
#define PLATFORM_VERSION @"2.4.2"

@interface RNTapResearch : RCTEventEmitter <RCTBridgeModule, TapResearchRewardDelegate, TapResearchSurveyDelegate, TapResearchPlacementDelegate>

@end
