package com.tdfm.word;


import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class Words {
    private static final String EMPTY_STRING = "";
    private final ConcurrentMap<String, Long> wordCount = Maps.newConcurrentMap();

    private final Translator translator;

    public Words(Translator t){
        this.translator = t;
    }

    public void add(final String... words) {
        if (words == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        List<String> wordsAsList = Arrays.asList(words.clone());
        if (wordsAsList.isEmpty()) {
            throw new IllegalArgumentException("Specify one or more words");
        } else if (wordsAsList.contains(null) || wordsAsList.contains(EMPTY_STRING)) {
            throw new IllegalArgumentException("Specified parameters must be valid words");
        } else if (wordsAsList.stream().anyMatch(word -> word.chars().anyMatch(ch -> !Character.isLetter(ch)))) {
            throw new IllegalArgumentException("Specified words must only contain alphabetic characters");
        }

        // convert to word by count upfront so we spend less time locking map in next step (fewer words to loop through)
        Map<String, Long> providedWordCount = wordsAsList
                .stream()
                .map(word -> translator.translate(word.toLowerCase())).
                collect(Collectors.groupingBy(k -> k, Collectors.counting()));

        // update count in threadsafe manner with minimal blocking
        providedWordCount.entrySet()
                .stream()
                .forEach(
                        word -> wordCount.merge(word.getKey(), word.getValue(), (prev, current) -> prev + current)
                );
    }

}
