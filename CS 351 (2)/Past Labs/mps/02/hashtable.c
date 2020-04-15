#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "hashtable.h"

/* Daniel J. Bernstein's "times 33" string hash function, from comp.lang.C;
   See https://groups.google.com/forum/#!topic/comp.lang.c/lSKWXiuNOAk */
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

static bucket_t *ht_get_node(hashtable_t *ht, char *key) {
  unsigned long idx = hash(key) % ht->size;
  bucket_t *b = ht->buckets[idx];
  while (b) {
    if (strcmp(b->key, key) == 0) {
      return b;
    }
    b = b->next;
  }
  return NULL;
}

void ht_put(hashtable_t *ht, char *key, void *val) {
  bucket_t *existing = ht_get_node(ht, key);
  if (existing) {
    free(key); // from strdup()
    if (existing->val) {
      free(existing->val);
    }
    existing->val = val;
    return;
  }

  unsigned long idx = hash(key) % ht->size;
  bucket_t *b = malloc(sizeof(bucket_t));
  b->key = key;
  b->val = val;
  b->next = ht->buckets[idx];
  ht->buckets[idx] = b;
}

void *ht_get(hashtable_t *ht, char *key) {
  bucket_t *node = ht_get_node(ht, key);
  if (!node) return NULL;
  return node->val;
}

static void ht_iter_bucket(hashtable_t *ht, int (*f)(bucket_t *, void *), void *ctx) {
  bucket_t *next;
  bucket_t *b;
  unsigned long i;
  for (i = 0; i < ht->size; i++) {
    b = ht->buckets[i];
    while (b) {
      next = b->next;
      if (!f(b, ctx)) {
        return ; // abort iteration
      }
      b = next;
    }
  }
}

static int ht_iter_f(bucket_t *b, void *ctx) {
  int (*f)(char *, void *) = ctx;
  f(b->key, b->val);
  return true;
}

void ht_iter(hashtable_t *ht, int (*f)(char *, void *)) {
  ht_iter_bucket(ht, ht_iter_f, f);
}

static void free_bucket(bucket_t *b) {
  free(b->key);
  free(b->val);
  free(b);
}

static int free_bucket_f(bucket_t *b, void *ignored) {
  free_bucket(b);
  return true;
}

void free_hashtable(hashtable_t *ht) {
  ht_iter_bucket(ht, free_bucket_f, NULL);
  free(ht->buckets);
  free(ht);
}

void ht_del(hashtable_t *ht, char *key) {
  unsigned long idx = hash(key) % ht->size;
  bucket_t **root = &ht->buckets[idx];
  bucket_t *cur = *root;
  bucket_t *prev = NULL;

  while (cur) {
    if (strcmp(key, cur->key) == 0) {
      if (!prev) {
        *root = cur->next;
      } else {
        prev->next = cur->next;
      }
      free_bucket(cur);
      return;
    }
    prev = cur;
    cur = cur->next;
  }
}

static int reinsert_f(bucket_t *b, void *ctx) {
  hashtable_t *new = ctx;
  ht_put(new, b->key, b->val);
  return true;
}

void ht_rehash(hashtable_t *ht, unsigned long newsize) {
  hashtable_t *ht_new = make_hashtable(newsize);
  ht_iter_bucket(ht, reinsert_f, ht_new);

  // free the old bucket_t containers
  bucket_t *tmp;
  bucket_t *cur;
  unsigned long i;
  for (i = 0; i < ht->size; i++) {
    cur = ht->buckets[i];
    while (cur) {
      tmp = cur->next;
      free(cur);
      cur = tmp;
    }
    ht->buckets[i] = NULL;
  }
  free(ht->buckets);

  ht->size = ht_new->size;
  ht->buckets = ht_new->buckets;

  // only free the temp container
  free(ht_new);
}
