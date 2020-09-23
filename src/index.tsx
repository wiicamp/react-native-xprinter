import { NativeModules } from 'react-native';

type XprinterType = {
  multiply(a: number, b: number): Promise<number>;
};

const { Xprinter } = NativeModules;

export default Xprinter as XprinterType;
