import { NativeModules } from 'react-native';

type XprinterType = {
  print(): Promise<boolean>;
};

const { Xprinter } = NativeModules;

export default Xprinter as XprinterType;
