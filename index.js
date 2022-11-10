import { NativeEventEmitter, NativeModules } from 'react-native';

export const PLACEMENT_CODE_SDK_NOT_READY = -1;

const { RNTapResearch } = NativeModules;
const tapResearchEmitter = new NativeEventEmitter(RNTapResearch);

// See: https://github.com/launchdarkly/react-native-client-sdk/issues/116
// Will be needed for react-native 0.80+
//class TREventEmitter extends NativeEventEmitter {
//  constructor() {
//    super(RNTapResearch);
//    this.ready = false;
//  }
//  addListener(eventType, listener, context) {
//    if (!this.ready) {
//      RNTapResearch.eventsNotifyReady(true);
//      this.ready = true;
//    }
//    RNTapResearch.eventsAddListener(eventType);
//
//    let subscription = super.addListener(`rntr_${eventType}`, listener, context);
//
//    // React Native 0.65+ altered EventEmitter:
//    // - removeSubscription is gone
//    // - addListener returns an unsubscriber instead of a more complex object with eventType etc
//
//    // make sure eventType for backwards compatibility just in case
//    subscription.eventType = `rntr_${eventType}`;
//
//    // New style is to return a remove function on the object, just in csae people call that,
//    // we will modify it to do our native unsubscription then call the original
//    let originalRemove = subscription.remove;
//    let newRemove = () => {
//      RNTapResearch.eventsRemoveListener(eventType, false);
//      if (super.removeSubscription != null) {
//        // This is for RN <= 0.64 - 65 and greater no longer have removeSubscription
//        super.removeSubscription(subscription);
//      } else if (originalRemove != null) {
//        // This is for RN >= 0.65
//        originalRemove();
//      }
//    };
//    subscription.remove = newRemove;
//    return subscription;
//  }
//
//  removeAllListeners(eventType) {
//    RNTapResearch.eventsRemoveListener(eventType, true);
//    super.removeAllListeners(`rntr_${eventType}`);
//  }
//
//  // This is likely no longer ever called, but it is here for backwards compatibility with RN <= 0.64
//  removeSubscription(subscription) {
//    RNTapResearch.eventsRemoveListener(subscription.eventType.replace('rntr_'), false);
//    if (super.removeSubscription) {
//      super.removeSubscription(subscription);
//    }
//  }
//}
//const tapResearchEmitter = new TREventEmitter();

export default RNTapResearch;

export { tapResearchEmitter };
