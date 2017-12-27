package com.accenture.ai.controller.alexa;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

public class HelloSpeechlet implements Speechlet {

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {


    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        return getResponse("HelloWorld","Welcome to Alexa Skill Kit, you can say hello");
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        SpeechletResponse ret = null;
        Intent intent = request.getIntent();
        if (intent.getName().equals("Hello")) {
            return getResponse("Saying hello","welcome");
        } else if (intent.getName().equals("HelloMe")) {
            return getResponse("Saying hello to you","Welcome" + intent.getSlot("Who").getValue());

        } else if (intent.getName().equals("Add")) {
            return getResponse("Adding numbers","The sum of the two numbers are " + (Integer.parseInt(intent.getSlot("NumberA").getValue())
            + Integer.parseInt(intent.getSlot("NumberB").getValue())));
        } else {
            ret = getResponse("Hmm","Are you kidding me？");
        }
        return ret;
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
    }


    private SpeechletResponse getResponse(String title,String response) {
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(response);
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(response);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
}
