import { NativeModules } from 'react-native';

type XprinterType = {
  print(text: String): Promise<boolean>;
};

const { Xprinter } = NativeModules;

export default Xprinter as XprinterType;
