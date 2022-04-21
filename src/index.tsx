import React, { Component } from 'react';
import { requireNativeComponent, UIManager, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-rn-gw-scanner' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

type RnGwScannerProps = {
  scanType: number;
  continuouslyScan: boolean;
  enableReturnOriginalScan: boolean;
  onNewScan: Function;
};

const ComponentName = 'RnGwScannerView';

export const RnGwScannerViewNative =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<RnGwScannerProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };

// @ts-ignore
export const ScanType = RnGwScannerViewNative.SCAN_TYPES;

// @ts-ignore
export class RnGwScannerView extends Component<RnGwScannerProps> {
  // @ts-ignore
  constructor(props) {
    super(props);
    this._onNewScan = this._onNewScan.bind(this);
  }
  // @ts-ignore

  _onNewScan(event) {
    // @ts-ignore
    if (!this.props.onNewScan) {
      return;
    }

    // @ts-ignore
    this.props.onNewScan(event.nativeEvent.message);
  }
  render() {
    return (
      // @ts-ignore
      <RnGwScannerViewNative {...this.props} onNewScan={this._onNewScan} />
    );
  }
}
