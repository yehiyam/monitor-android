//package com.example.android.camera2basic;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.google.zxing.Result;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.IOException;
//
//import me.dm7.barcodescanner.zxing.ZXingScannerView;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Response;
//
//public class QrCodeScannerFragment implements ZXingScannerView.ResultHandler {
//    private ZXingScannerView mScannerView;
//
//
//    public static QrCodeScannerFragment newInstance() {
//        return new QrCodeScannerFragment();
//    }
//
//    private View loadingPanel;
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
////        mScannerView = new ZXingScannerView(getContext());
////        return mScannerView;
//        View view = inflater.inflate(R.layout.fragment_qr_scanner, container, false);
//        loadingPanel = view.findViewById(R.id.loadingPanel);
//        mScannerView = view.findViewById(R.id.qr_scanner);
//        return view;
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Register ourselves as a handler for scan results.
//        mScannerView.setResultHandler(this);
//        // Start camera on resume
//        mScannerView.startCamera();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        // Stop camera on pause
//        mScannerView.stopCamera();
//    }
//
//    @Override
//    public void handleResult(Result rawResult) {
//        String deviceId = rawResult.getText();
////        deviceId = "cvmonitors-ivac-3d2bff4aa67b4285";
//        loadingPanel.setVisibility(View.VISIBLE);
//        ((MainActivity)getActivity()).getNetworkManager().getMonitorData(deviceId, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//
//                Gson gson = new GsonBuilder()
//                        .serializeNulls()
//                        .registerTypeAdapter(SickData.class, new GsonSerializations.CroppingHashMapDeSerializer())
//                        .create();
//
//                SickData sickDataRes = null;
//
//                try {
//                     sickDataRes = gson.fromJson(response.body().string(), SickData.class);
//                }
//                catch (JsonSyntaxException e) {
//                    QrCodeScannerFragment.this.replaceFragmentsWithoutStack(DeviceDataProblemFragment.class);
//                }
//
//                if(sickDataRes == null){
//                    QrCodeScannerFragment.this.replaceFragmentsWithoutStack(DeviceDataProblemFragment.class);
//                } else {
//                    final SickData finalSickDataRes = sickDataRes;
//                    QrCodeScannerFragment.this.getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((MainActivity)QrCodeScannerFragment.this.getActivity()).setSickDataVM(finalSickDataRes);
//                        }
//                    });
//                    QrCodeScannerFragment.this.replaceFragmentsWithoutStack(ChoicesSetFragment.class);
//                }
//            }
//        });
//
//    }
//
//
//}