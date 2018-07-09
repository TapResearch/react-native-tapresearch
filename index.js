import { NativeEventEmitter, NativeModules } from 'react-native';

export const PLACEMENT_CODE_SDK_NOT_READY = -1;

const { RNTapResearch } = NativeModules;
const tapResearchEmitter = new NativeEventEmitter(RNTapResearch);

export default RNTapResearch;
export { tapResearchEmitter };
