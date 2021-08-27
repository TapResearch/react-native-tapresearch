#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <TapResearchSDK/TapResearchSDK.h>

#define PLATFORM @"react"
#define PLATFORM_VERSION @"2.2.1"

@interface RNTapResearch : RCTEventEmitter <RCTBridgeModule, TapResearchRewardDelegate, TapResearchSurveyDelegate, TapResearchPlacementDelegate>

@end
