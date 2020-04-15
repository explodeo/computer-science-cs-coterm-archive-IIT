import networkx as nx
import matplotlib.pyplot as plt
import timeit
def dijkstra(graph, start, end):
    sta = timeit.default_timer()
    # empty dictionary to hold weights
    weights = {} 
    # list of vertices in path to current vertex
    predecessors = {} 
    
    # get all the nodes that need to be assessed
    to_assess = graph.keys() 

    # set all initial weights to infinity
    #  and no predecessor for any node
    for node in graph:
        weights[node] = float('inf')
        predecessors[node] = None
    
    # set the initial collection of 
    # permanently labelled nodes to be empty
    scannedNodes = []

    # set the distance from the start node to be 0
    weights[start] = 0
    
    # as long as there are still nodes to assess:
    while len(scannedNodes) < len(to_assess):

        # chop out any nodes with a permanent label
        toScan = {node: weights[node]\
                    for node in [node for node in\
                    to_assess if node not in scannedNodes]}

        # find the closest node to the current node
        closest = min(toScan, key = weights.get)

        # and add it to the permanently labelled nodes
        scannedNodes.append(closest)
        
        # then for all the neighbours of 
        # the closest node (that was just added to
        # the permanent set)
        for node in graph[closest]:
            # if a shorter path to that node can be found
            if weights[node] > weights[closest] +\
                       graph[closest][node]:

                # update the distance with 
                # that shorter distance
                weights[node] = weights[closest] +\
                       graph[closest][node]

                # set the predecessor for that node
                predecessors[node] = closest
                
    # once the loop is complete the final 
    # path needs to be calculated - this can
    # be done by backtracking through the predecessors
    path = [end]
    while start not in path:
        path.append(predecessors[path[-1]])
    
    sto = timeit.default_timer()
    tim=sto-sta
    # return the path in order start -> end, and it's cost
    return path[::-1], weights[end]

def all_simple_paths(G, source, target, cutoff=None):
    
    #if source not in G:
        #raise nx.NodeNotFound('source node %s not in graph' % source)
    if target in G: 
        targets = {target}
    #else:
        #try:
            #targets = set(target)
        #except TypeError:
            #raise nx.NodeNotFound('target node %s not in graph' % target)
    if source in targets:
        return []
    if G.is_multigraph():
        return _all_simple_paths_multigraph(G, source, targets, cutoff)
    else:
        return _all_simple_paths_graph(G, source, targets, cutoff)

def _all_simple_paths_graph(G, source, targets, cutoff):
    visited = collections.OrderedDict.fromkeys([source])
    stack = [iter(G[source])]
    while stack:
        nxt = next(stack[-1], None)
        if nxt is None:
            stack.pop()
            visited.popitem()
        elif len(visited) < cutoff:
            if nxt in visited:
                continue
            if nxt in targets:
                yield list(visited) + [nxt]
            visited[nxt] = None
            if targets - set(visited.keys()):  # expand stack until find all targets
                stack.append(iter(G[nxt]))
            else:
                visited.popitem()  # maybe other ways to nxt
        else:  # len(visited) == cutoff:
            for target in (targets & (set(stack[-1]) | {nxt})) - set(visited.keys()):
                yield list(visited) + [target]
            stack.pop()
            visited.popitem()

            """
depth = len(matrix)-1 
    G = nx.to_dict_of_dicts(graph)
    visited = oDict(G[s]) #store list of visited items
    d = [d]
    stk = [iter(G[s])]
    while stk:
        nxt = next(stk[-1], None)
        if nxt is None:
            stk.pop()
            visited.popitem()
        elif len(visited) != depth:
            if nxt in visited: continue
            if nxt in d: yield list(visited) + [nxt]
            visited[nxt] = None
            if d - set(visited.keys()): stk.append(iter(G[nxt]))
            else:  visited.popitem()  # maybe other ways to nxt
        else:  # len(visited) == cutoff:
            for target in (d & (set(stk[-1]) | {nxt})) - set(visited.keys()):
                yield list(visited) + [target]
            stk.pop()
            visited.popitem()

            """