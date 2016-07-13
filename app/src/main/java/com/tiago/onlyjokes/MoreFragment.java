package com.tiago.onlyjokes;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MoreFragment extends DialogFragment {
    ImageView quickVocabBtn, engPillsBtn, questionPromptBtn, quickPronBtn;
    LinearLayout emailLL;
    ImageView logoIV;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            //View view = inflater.inflate(R.layout.more_fragment, null, false);
            View view = inflater.inflate(R.layout.about_fragment,container);
            TextView versionTV = (TextView)view.findViewById(R.id.versionTV);
            versionTV.setText("version: "+MainActivity.newVersion);

            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            emailLL = (LinearLayout)view.findViewById(R.id.emailLL);
            quickVocabBtn = (ImageView)view.findViewById(R.id.quick_vocab_BT);
            engPillsBtn = (ImageView)view.findViewById(R.id.eng_pills_BT);
            questionPromptBtn = (ImageView)view.findViewById(R.id.question_prompt_BT);
            quickPronBtn = (ImageView)view.findViewById(R.id.quick_pron_BT);
            logoIV = (ImageView)view.findViewById(R.id.logoIV);
            quickVocabBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoToMarket gtm = new GoToMarket();
                    gtm.goToAppStore(getActivity(), "com.tiago.quickvocabnotes");
                }
            });
            engPillsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoToMarket gtm = new GoToMarket();
                    gtm.goToAppStore(getActivity(), "com.colombo.tiago.esldailyshot");
                }
            });
            questionPromptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoToMarket gtm = new GoToMarket();
                    gtm.goToAppStore(getActivity(), "com.tiago.questionprompt");
                }
            });
            quickPronBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoToMarket gtm = new GoToMarket();
                    gtm.goToAppStore(getActivity(), "com.tiago.tspeak");
                }
            });
            //emailLL.setOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View view) {
            //        ((MainActivity)getActivity()).sendEmail();
            //    }
            //});
            logoIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //((MainActivity)getActivity()).sendEmail();
                    GoToMarket gtm = new GoToMarket();
                    gtm.goToDevPage(getActivity());
                }
            });



            return view;

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {

            super.onActivityCreated(savedInstanceState);
        }
    }

