package com.example.prueba002;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentNoti extends Fragment {

    LinearLayout container;
    private ImageView backButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containerF, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noti, containerF, false);

        container = view.findViewById(R.id.llNotificaciones);
        backButton = view.findViewById(R.id.back_arrow);
        mostrarNotificaciones();

        backButton.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return view;

    }



    private void mostrarNotificaciones() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("notificaciones", Context.MODE_PRIVATE);




        container.removeAllViews();

        for (int i = 1; i <= 3; i++) {
            String noti = prefs.getString("noti" + i, null);
            String fecha = prefs.getString("fecha" + i, null);

            if (noti != null) {

                CardView card = new CardView(getContext());
                LinearLayout.LayoutParams paramsCard = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsCard.setMargins(0, 0, 0, 16);
                card.setLayoutParams(paramsCard);
                card.setRadius(12f);
                card.setCardBackgroundColor(0xFFFFFFFF);
                card.setCardElevation(6f);
                card.setPadding(16,16,16,16);


                LinearLayout ll = new LinearLayout(getContext());
                ll.setOrientation(LinearLayout.VERTICAL);


                TextView tvNoti = new TextView(getContext());
                tvNoti.setText("• " + noti);
                tvNoti.setTextSize(16f);
                tvNoti.setTextColor(0xFF333333);

                ll.addView(tvNoti);


                if(fecha != null){
                    TextView tvFecha = new TextView(getContext());
                    tvFecha.setText(fecha);
                    tvFecha.setTextSize(12f);
                    tvFecha.setTextColor(0xFF777777);
                    ll.addView(tvFecha);
                }

                card.addView(ll);
                container.addView(card);
            }
        }
    }

    private final android.content.BroadcastReceiver receiver = new android.content.BroadcastReceiver() {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {

            mostrarNotificaciones();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().registerReceiver(receiver, new android.content.IntentFilter("NUEVA_NOTIFICACION"));
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(receiver);
    }
}
