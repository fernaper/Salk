import sys

"""

Class that with an aspell dictionary, will clean a freq word list of a language
from anglicisms or words that doesn't exist

"""

#Next paths are filled in main so there is no need to change anything in the variables

# Freq list file path
FREQ_WORDS_PATH = 'en_50k.txt'

# Dictionary file path
DICTIONARY_PATH = 'en.txt'

# Clean words list file path
CLEAN_WORDS_PATH = 'en_clean.txt'

# Code of the language we want to clean the words
LANG_CODE = ''


def dict_to_txt(words, path):
    """Write a dic in a txt file

        :param words:          -- dic with the cleaning words we want to write
        :param path:         -- path where we want to store the words

    """

    with open(path, 'w') as file:
        for line in words:
            file.write(line + '\n')


def txt_to_dict(path):
    """Write a dic in a txt file
            :param path:                 -- path where the file of freq list is
            :return words2clean:         -- dict with the freq words we want to write

    """

    freq_position = 1
    words2clean = dict()

    with open(path, 'r') as f:
        for word in f.readlines():
            words2clean[word.split()[0]] = freq_position
            freq_position += 1

    return words2clean


def main():
    global DICTIONARY_PATH, FREQ_WORDS_PATH, CLEAN_WORDS_PATH, LANG_CODE


    with open(DICTIONARY_PATH, 'r') as f:
        dictionary_words = [x.strip() for x in f.readlines()]
    f.close()


    words2clean = txt_to_dict(FREQ_WORDS_PATH)

    clean_words = set()

    for word in words2clean:
        if word in dictionary_words:
            if not word[0].isupper():
                clean_words.add(word)

    dict_to_txt(clean_words, CLEAN_WORDS_PATH)


if __name__ == "__main__":
    main()
