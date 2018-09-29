
# coding: utf-8

# In[1]:
import sys

#print ("This is the name of the script: ", sys.argv[0])
#print ("Number of arguments: ", len(sys.argv))
#print ("The arguments are: " , str(sys.argv))

import nltk
from nltk.tokenize import sent_tokenize, word_tokenize
from nltk import ne_chunk, pos_tag


# In[2]:


text = "Hi lawbot. What is the Registration processing for brand New Cars."

if len(sys.argv) > 1:
	text = sys.argv[1];

#text = "How do I claim my ETF."
#text = "i was in a car accident and it was not my fault"
#text = "what is the procedure for getting a driving licence"
#text = "I lost my NIC what should I do?"


# In[3]:


sentences =sent_tokenize(text)


# In[4]:


words = word_tokenize(text)


# In[5]:


# In[6]:


postagged = pos_tag(words)


# In[7]:


tags = pos_tag(words)


# In[8]:


chunk = ne_chunk(tags)


# In[9]:


# In[10]:


from nltk.corpus import stopwords
stopwords.words('english')


# In[11]:


clean_tokens = words[:]
sr = stopwords.words('english')
for token in words:
    if token in stopwords.words('english'):
        clean_tokens.remove(token)

freq = nltk.FreqDist(clean_tokens)
#for key,val in freq.items():
#    print (str(key) + ':' + str(val))



# In[12]:


from nltk.corpus import wordnet
syn = wordnet.synsets("pain")



# In[13]:


from nltk.stem import PorterStemmer

stemmer = PorterStemmer()
stemmed = []

for token in clean_tokens:
    stemmed.append(stemmer.stem(token))

# In[14]:


from nltk.stem import WordNetLemmatizer
 
lemmatizer = WordNetLemmatizer()
lemmatized = []

postagged = pos_tag(clean_tokens)

numrows = len(postagged)    # rows 
numcols = len(postagged[0]) # columns 

for i in range(0,numrows):
    type = "x"
    if postagged[i][1][0] == "J":
        #print(postagged[i][1])
        type = "a" #adjective
    elif postagged[i][1][0] == "V":
        #print(postagged[i][1])
        type = "v" #verb
    elif postagged[i][1][0] == "N":
        #print(postagged[i][1])
        type = "n" #noun
    elif postagged[i][1][0] == "R":
        #print(postagged[i][1])
        type = "r" #adverb
    
    if type == "x":
        lemmatized.append(postagged[i][0])
    else:
        #print("f")
        lemmatized.append(lemmatizer.lemmatize(postagged[i][0], pos=type))

print(lemmatized)

