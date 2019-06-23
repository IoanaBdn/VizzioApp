package com.example.ioana.vizzioapp;

import java.util.HashMap;

public class BrailleDecoder
{

    public HashMap<String, Character> lettersSymbolsHashMap;
    public HashMap<String, Character> capitalLettersSymbolsHashMap;
    public HashMap<String, Character> numbersHashMap;

    public BrailleDecoder()
    {
        initializeCharactersHashMap();
    }

    private void initializeCharactersHashMap()
    {
        //LETTERS
        lettersSymbolsHashMap = new HashMap<>();

        lettersSymbolsHashMap.put("100000",'a');
        lettersSymbolsHashMap.put("110000",'b');
        lettersSymbolsHashMap.put("100100",'c');
        lettersSymbolsHashMap.put("100110",'d');
        lettersSymbolsHashMap.put("100010",'e');
        lettersSymbolsHashMap.put("110100",'f');
        lettersSymbolsHashMap.put("110110",'g');
        lettersSymbolsHashMap.put("110010",'h');
        lettersSymbolsHashMap.put("010100",'i');
        lettersSymbolsHashMap.put("010110",'j');
        lettersSymbolsHashMap.put("101000",'k');
        lettersSymbolsHashMap.put("111000",'l');
        lettersSymbolsHashMap.put("101100",'m');
        lettersSymbolsHashMap.put("101110",'n');
        lettersSymbolsHashMap.put("101010",'o');
        lettersSymbolsHashMap.put("111100",'p');
        lettersSymbolsHashMap.put("111110",'q');
        lettersSymbolsHashMap.put("111010",'r');
        lettersSymbolsHashMap.put("011100",'s');
        lettersSymbolsHashMap.put("011110",'t');
        lettersSymbolsHashMap.put("101001",'u');
        lettersSymbolsHashMap.put("111001",'v');
        lettersSymbolsHashMap.put("010111",'w');
        lettersSymbolsHashMap.put("101101",'x');
        lettersSymbolsHashMap.put("101111",'y');
        lettersSymbolsHashMap.put("101011",'z');
        //SYMBOLS
        lettersSymbolsHashMap.put("010000",',');
        lettersSymbolsHashMap.put("011000",';');
        lettersSymbolsHashMap.put("010010",':');
        lettersSymbolsHashMap.put("010011",'.');

        lettersSymbolsHashMap.put("011010",'!');
        lettersSymbolsHashMap.put("011001",'?');
        lettersSymbolsHashMap.put("011011",'@');
        lettersSymbolsHashMap.put("000011",'-');
        lettersSymbolsHashMap.put("001011",'"');
        lettersSymbolsHashMap.put("000100",' ');
        lettersSymbolsHashMap.put("000001",'~');


        //CAPITAL LETTERS
        capitalLettersSymbolsHashMap = new HashMap<>();

        capitalLettersSymbolsHashMap.put("100000",'A');
        capitalLettersSymbolsHashMap.put("110000",'B');
        capitalLettersSymbolsHashMap.put("100100",'C');
        capitalLettersSymbolsHashMap.put("100110",'D');
        capitalLettersSymbolsHashMap.put("100010",'E');
        capitalLettersSymbolsHashMap.put("110100",'F');
        capitalLettersSymbolsHashMap.put("110110",'G');
        capitalLettersSymbolsHashMap.put("110010",'H');
        capitalLettersSymbolsHashMap.put("010100",'I');
        capitalLettersSymbolsHashMap.put("010110",'J');
        capitalLettersSymbolsHashMap.put("101000",'K');
        capitalLettersSymbolsHashMap.put("111000",'L');
        capitalLettersSymbolsHashMap.put("101100",'M');
        capitalLettersSymbolsHashMap.put("101110",'N');
        capitalLettersSymbolsHashMap.put("101010",'O');
        capitalLettersSymbolsHashMap.put("111100",'P');
        capitalLettersSymbolsHashMap.put("111110",'Q');
        capitalLettersSymbolsHashMap.put("111010",'R');
        capitalLettersSymbolsHashMap.put("011100",'S');
        capitalLettersSymbolsHashMap.put("011110",'T');
        capitalLettersSymbolsHashMap.put("101001",'U');
        capitalLettersSymbolsHashMap.put("111001",'V');
        capitalLettersSymbolsHashMap.put("010111",'W');
        capitalLettersSymbolsHashMap.put("101101",'X');
        capitalLettersSymbolsHashMap.put("101111",'Y');
        capitalLettersSymbolsHashMap.put("101011",'Z');
        //SYMBOLS
        capitalLettersSymbolsHashMap.put("010000",',');
        capitalLettersSymbolsHashMap.put("011000",';');
        capitalLettersSymbolsHashMap.put("010010",':');
        capitalLettersSymbolsHashMap.put("010011",'.');

        capitalLettersSymbolsHashMap.put("011010",'!');
        capitalLettersSymbolsHashMap.put("011001",'?');
        capitalLettersSymbolsHashMap.put("011011",'@');
        capitalLettersSymbolsHashMap.put("000011",'-');
        capitalLettersSymbolsHashMap.put("001011",'"');
        capitalLettersSymbolsHashMap.put("000100",' ');
        capitalLettersSymbolsHashMap.put("000001",'~');


        //NUMBERS
        numbersHashMap = new HashMap<>();

        numbersHashMap.put("010110",'0');
        numbersHashMap.put("100000",'1');
        numbersHashMap.put("110000",'2');
        numbersHashMap.put("100100",'3');
        numbersHashMap.put("100110",'5');
        numbersHashMap.put("100010",'5');
        numbersHashMap.put("110100",'6');
        numbersHashMap.put("110110",'7');
        numbersHashMap.put("110010",'8');
        numbersHashMap.put("010100",'9');
        numbersHashMap.put("000100",' ');
        numbersHashMap.put("000001",'~');

    }


    public Character decodeBrailleStringToChar(String binaryBraille, boolean isCaps, boolean isNumeric)
    {
        Character character = null;
        if(isCaps)
        {
            if(isNumeric)
            {
                character = numbersHashMap.get(binaryBraille);
            }
            else
            {
                character = capitalLettersSymbolsHashMap.get(binaryBraille);
            }
        }
        else
        {
            if(isNumeric)
            {
                character = numbersHashMap.get(binaryBraille);
            }
            else
            {
                character = lettersSymbolsHashMap.get(binaryBraille);
            }
        }

        if(character == null)
        {
            character = Character.MIN_VALUE;
        }
        return character;
    }

}
