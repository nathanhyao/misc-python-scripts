'''
This console program was created because I wanted to improve my typing speed.
Despite playing a bunch of typeracer (https://play.typeracer.com/) and
monkeytype (https://monkeytype.com/), it felt like my speed was plateauing,
apparently because I kept frequently mistyping a set of specific characters.
'''

import random

# instructions
print("This console program improves typing agility by tackling missed letters.")
print("\nEnter 0 to quit the program (results are not saved).")
print("\nEnter 1 to practice your missed letters.")

# initial declarations
ALPHABET = "abcdefghijklmnopqrstuvwxyz" # alphabet characters reference
alphabet_list = [] # alphabet characters list
wrong_list = [] # list of typed-wrong characters
english_words = [] # english words for typing practice

for l in ALPHABET:
    alphabet_list.append(l.upper())

# user interaction
while True:
    wrong_letter = input("\nMissed letter: ").upper()

    if len(wrong_letter) > 1: # more than one character entered
        print("Enter characters one at a time!")

    elif wrong_letter.isalpha() == False: # character is not a letter

        if wrong_letter.isdigit():

            if int(wrong_letter) == 0: # chosen to quit the program
                print("Ending program...")
                break

            elif int(wrong_letter) == 1: # chosen to start practice session

                if len(wrong_list) == 0:
                    print("\nFirst enter some characters you've typed wrong.")
                else:
                    print("\nPractice session has begun (0 to quit practice).")

                with open("words.txt", encoding="utf8") as fo:
                    l = fo.readlines()
                    for w in l:
                        english_words.append(w.strip())

                random.shuffle(english_words) # prepare words for typing
                wrong_set = set(wrong_list) # remove duplicates of wrong characters
                answer = ""

                num_words_practiced = 0
                for w in english_words:
                    for l in wrong_set:

                        if l.lower() in w:
                            print(f"\nType '{w}' (lowercase):".ljust(45), end = '')
                            answer = input()

                            if answer == w:
                                print("CORRECT")
                                num_words_practiced += 1
                            elif answer.isalpha() == False: # not a character?

                                if answer.isdigit() and int(answer) == 0:
                                    print(f"You accurately typed {num_words_practiced} word(s)!")
                                    print("Practice session has been quit.")
                                    break

                                print("Invalid answer!")
                            else:
                                print("INCORRECT")

                    if answer.isdigit() and int(answer) == 0:
                        break # breaks out of 2nd for loop
            else:
                print("Invalid answer!") # digit is not 1 or 0
        else:
            print("Invalid answer!") # not a digit or a letter
    else:
        wrong_list.append(wrong_letter) # user entered a letter to practice

        for i in range(len(ALPHABET)): # display a list of letters of interest
            if wrong_list.count(alphabet_list[i]) > 0:
                print(f"  {alphabet_list[i]}...{wrong_list.count(alphabet_list[i])}")
