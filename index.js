import { NativeEventEmitter, NativeModules } from 'react-native';

const { RNTapResearch } = NativeModules;
const tapResearchEmitter = new NativeEventEmitter(RNTapResearch);

export default RNTapResearch;
export { tapResearchEmitter };