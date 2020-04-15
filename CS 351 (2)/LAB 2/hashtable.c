#include <stdlib.h>
#include <string.h>
#include "hashtable.h"

unsigned long hash(char *str) {
  unsigned long hash = 5381;
  int c;
  while ((c = *str++))
    hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
  return hash;
}

hashtable_t *make_hashtable(unsigned long size) {
  hashtable_t *ht = malloc(sizeof(hashtable_t));
  ht->size = size;
  ht->buckets = calloc(sizeof(bucket_t *), size);
  return ht;
}

void ht_put(hashtable_t *ht, char *key, void *val){
  unsigned int idx = hash(key) % ht->size;
  bucket_t *iter = ht->buckets[idx];
  while(iter){  
    if (!strcmp(key, iter->key)){ //STRCMP RETURNS ZERO IF STRINGS EQUAL OMG......
      free(iter->key);
      free(iter->val);
      iter->key = key;
      iter->val = val;
      return;
    }
    iter = iter->next;
  }
  bucket_t *b = malloc(sizeof(bucket_t)); //makes new key-val pair and adds it to l-list
  b->key = key;
  b->val = val;
  b->next = ht->buckets[idx];
  ht->buckets[idx] = b;
}

void *ht_get(hashtable_t *ht, char *key) {
  unsigned int idx = hash(key) % ht->size;
  bucket_t *b = ht->buckets[idx];
  while (b) {
    if (strcmp(b->key, key) == 0) {
      return b->val;
    }
    b = b->next;
  }
  return NULL;
}

void ht_iter(hashtable_t *ht, int (*f)(char *, void *)) {
  bucket_t *b;
  unsigned long i;
  for (i=0; i<ht->size; i++) {
    b = ht->buckets[i];
    while (b) {
      if (!f(b->key, b->val)) {
        return ; // abort iteration
      }
      b = b->next;
    }
  }
}

void free_hashtable(hashtable_t *ht) {
  bucket_t *b;
  unsigned long i;
  bucket_t *to_del;
  for (i=0; i<ht->size; i++) {
    b = ht->buckets[i];
    to_del = b;
    while (b) {
      to_del = b;
      b = b->next;
      free(to_del->key);
      free(to_del->val);
      free(to_del);
    }
  }
  free(ht->buckets);
  free(ht); 
}

/* TODO */
void ht_del(hashtable_t *ht, char *key) {
  unsigned int idx = hash(key) % ht->size;
  bucket_t *prev = ht->buckets[idx];
  bucket_t *cur = prev->next;
  if(!strcmp(prev->key,key)){  //if a root needs deleting (STRCMP eval to ZERO!)
    ht->buckets[idx] = cur;
    free(prev->key);
    free(prev->val);
    free(prev);
    //free(key);
    return;
  }
  while(cur){ //if element n needs deleting
    if(!strcmp(cur->key,key)){ //if strings equal
      free(cur->key);
      free(cur->val);
      prev->next = cur->next;
      free(cur);
      //free(key);
      return;
    }
    cur = cur->next;
    prev = prev->next;
  }
}

void ht_rehash(hashtable_t *ht, unsigned long newsize) {
  hashtable_t *new_ht = make_hashtable(newsize);
  unsigned long i;
  bucket_t *b;
  bucket_t *to_del;
  char *key;
  void *value;
  for (i=0; i<ht->size; i++){
    b = ht->buckets[i];
    while (b){
      key = malloc(*(b->key)+1);
      value = malloc(strlen(b->val)+1);
      strcpy(key, b->key);
      strcpy(value, b->val);
      ht_put(new_ht, key, value);
      to_del = b;
      b = b->next;
      free(to_del->key);
      free(to_del->val);
      free(to_del);
    }
  }
  free(ht->buckets);
  ht->buckets = new_ht->buckets;
  ht->size = new_ht->size;
  free(new_ht);
}
