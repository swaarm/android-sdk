package com.swaarm.sdk.breakpoint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Base64;
import android.view.View;
import android.view.ViewTreeObserver;

import com.swaarm.sdk.common.Consumer;
import com.swaarm.sdk.common.DeviceInfo;

import java.io.ByteArrayOutputStream;

public class BreakpointScreenshotCapture {

    private final DeviceInfo deviceInfo;
    private final BreakpointAppSetIdRepository breakpointAppSetIdRepository;

    public BreakpointScreenshotCapture(
            DeviceInfo deviceInfo,
            BreakpointAppSetIdRepository breakpointAppSetIdRepository
    ) {
        this.deviceInfo = deviceInfo;
        this.breakpointAppSetIdRepository = breakpointAppSetIdRepository;
    }

    public void takeScreenshot(View view, Consumer<String> screenshotConsumer) {
        if (!breakpointAppSetIdRepository.hasId(deviceInfo.getAppSetId())) {
            return;
        }

        if (view.getWidth() == 0 || view.getHeight() == 0) {
            //wait for the view to complete rendering
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    deregisterListener();
                    if (view.getWidth() == 0 || view.getHeight() == 0) {
                        return;
                    }
                    screenshotConsumer.accept(captureBase64Screenshot(view));
                }

                private void deregisterListener() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
            return;
        }
        screenshotConsumer.accept(captureBase64Screenshot(view));
    }

    private String captureBase64Screenshot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        return "data:image/jpg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
}
