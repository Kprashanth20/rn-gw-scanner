import * as React from 'react';

import { StyleSheet, View } from 'react-native';
import { RnGwScannerView } from 'react-native-rn-gw-scanner';

export default function App() {
  return (
    <View style={styles.container}>
      <RnGwScannerView
        style={styles.box}
        onNewScan={(scan) => {
          console.log(scan);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
