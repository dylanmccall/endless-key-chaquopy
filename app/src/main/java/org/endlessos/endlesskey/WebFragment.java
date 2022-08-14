package org.endlessos.endlesskey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import org.endlessos.endlesskey.databinding.WebFragmentBinding;

public class WebFragment extends Fragment {

    private WebFragmentBinding binding;
    private WebViewClient webViewClient;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        EndlessKeyApplication application = (EndlessKeyApplication) getActivity().getApplication();

        binding = WebFragmentBinding.inflate(inflater, container, false);

        webViewClient = new WebViewClient();
        binding.webview.setWebViewClient(webViewClient);

        WebSettings webSettings = binding.webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);

        application.kolibriBaseUrl.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                // TODO: Instead of all this, use a custom x-kolibri-internal protocol handler which
                //  proxies to kolibriBaseUrl.
                EndlessKeyApplication application = (EndlessKeyApplication) getActivity().getApplication();
                binding.webview.loadUrl(application.kolibriBaseUrl.get());
            }
        });
        application.kolibriBaseUrl.notifyChange();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}