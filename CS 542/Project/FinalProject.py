###		Christopher Morcom		###
###		CS 542 Final Project	###
###			Spring 2019			###
###			Michael Choi 		###

import re
import sys
import os
import networkx as nx
from math import floor
import _thread
import msvcrt as m
from collections import defaultdict
from collections import OrderedDict as oDict
from tkinter import filedialog
from tkinter import Frame, Canvas
from tkinter.ttk import *
from tkinter import *
import matplotlib.pyplot as plt
import re

######## HELPER FUNCTIONS ########

def wait():
	print("\n[Press any key to continue...]")
	m.getch()
	
def clearcmdline(): 
	clear = lambda: os.system('"cls')
	clear()

def isInt(s):
	try:
		int(s)
		return True
	except:
		return False

def checkInputs(rList):
	for x in rList:
		if not isInt(x):
			return False
	return True

def getCommandOption():
	cmd = m.getch() #bad command handled in main()
	print(str(chr(cmd[0])))
	if cmd:
		return str(chr(cmd[0]))
	else:
		return 'invalid'

######## COMMAND FUNCTIONS ########

def initialize():
	print("\nCS 542 Link State Routing Simulator (by CHRISTOPHER MORCOM)\n", end ="", flush=True)
	print("""
	(1) Input a Network Topology 
	(2) Generate Forwarding Tables for All Routers
	(3) Paths from Source to Destination
	(4) Update Network Topology
	(5) Best Router for Broadcast
	(6) Exit\n""", flush=True)
	print('Command: ', end="", flush=True)
	cmd = getCommandOption()
	return cmd

def readMatrix(ma):
	done = False
	while(not done):
		#read file by opening it from a dialog box
		rdir = Tk()
		rdir.title("Get Matrix File")
		Label(rdir, text="Matrix file must be delimited in tabs, spaces, or commas.\nHit cancel to refresh the existing topology.").grid(row=0)
		#Label(rdir, text=".").grid(row=x)
		rdir.file =  filedialog.askopenfilename(initialdir = "/",title = "Select Matrix Topology File",filetypes = (("All Files", "*.*"),("Text Document","*.txt"),("Comma-Seprated Values",".csv"),("Tab-Separated Values",".tsv")))
		if rdir.file:
			done = True
			fname = str(rdir.file)
			rdir.destroy()
		elif ma: #allows cancelling if matrix exists
			done=True
			rdir.destroy()
			return
		else:
			print("Please Select an Input File!")
			rdir.destroy()
	file = open(fname)
	print( "Reading: ", fname)
	NW = list()
	#read fie into 2D array 
	if fname.endswith(".txt") or fname.endswith(".csv") or fname.endswith(".tsv"):
		for line in file:
			#split by any char not a decimal or symbol
			ax = list(re.split(',|[A-z]|\s*',line.strip()))
			#print(ax)
			NW.append(ax)
		showTopology(NW)
		file.close()
		return NW
	else:
		print("File Cannot Be Read. \nFile must not have headers or footers, or a byte-order mark.")
		print("Closing Simulator\n\n")
		exit(0)
def showTopology(matrix): #review NW topology by rebuilding from list
	print("Review matrix topology:")
	for x in matrix:
		for y in x: 
			print('\t',y,end="", flush=True)
		print("")

def buildGraph(G, matrix):
	G.clear()
	G.add_nodes_from(range(1,len(matrix)+1))
	for x in range(len(matrix)):
		for y in range(len(matrix[x])): 
			if x<y: #add edges based on upper triangle of the matrix (matrix should be (A))
				if int(matrix[x][y]) != -1:
					G.add_edge(x+1,y+1,weight=int(matrix[x][y]))
					#print("added edge {},{}, weight = {}".format(x+1,y+1,matrix[x][y]))

def displayGraph(G):
	try:
		plt.clf()
		pos=nx.spring_layout(G)
		nx.draw(G,pos, with_labels=True)
		labels = nx.get_edge_attributes(G,'weight')
		nx.draw_networkx_edge_labels(G,pos,edge_labels=labels)
		#fig = plt.figure()
		#fig.canvas.draw()
		plt.show()
	except:
		pass

def showConnections(matrix): #build all forwarding tables in window
	#clearcmdline()
	connections = []
	master = Tk() #master window
	master.title("List of all Connection Tables")
	w=Canvas(master) #holds all frames
	scroll_y = Scrollbar(master, orient="vertical", command=w.yview) #scrollbar for more than 15 nodes
	frames = []
	#create each forwarding Table
	for ctables in range(len(matrix)):
		f = Frame(w, bd=2, relief=RIDGE)
		Label(f,text="Router {} Connection Table".format(ctables+1),anchor=CENTER).grid(row=0,columnspan=2)
		LCol = ""
		for x in range(len(matrix)): #left column
			LCol = LCol+str(x)+"\n"
		RCol = "" #right column
		for x in matrix[ctables]:
			if x == '0':
				RCol+="-\n"
			elif x == '-1':
				RCol+="∞\n"
			else: 
				RCol=RCol+str(x)+"\n"
		Label(f,text="Destination",anchor=CENTER).grid(column=0,row=1)
		Label(f,text="Cost",anchor=CENTER).grid(column=1,row=1)
		Label(f,text="------------------------",anchor=CENTER).grid(row=2,columnspan=2)
		Label(f,text=LCol,anchor=CENTER).grid(column=0,row=3)
		Label(f,text=RCol,anchor=CENTER).grid(column=1,row=3)
		frames.append(f)
	cols=0
	#place fwd tables on canvas
	for ff in frames:
		ff.grid(row = floor(cols/5), column = cols%5, sticky = W+E+N+S, padx=20, pady=20)
		cols+=1
	w.update_idletasks() #force update canvas
	w.configure(scrollregion=w.bbox('all'), yscrollcommand=scroll_y.set) #create scrollbar
	w.pack(fill='both', expand=True, side='left') #pack canvas to window
	scroll_y.pack(fill='y', side='right') #pack scrollbar to window
	w.mainloop() #start thread on return

def routerAdd(matrix):
	clearcmdline()
	#get router input
	print("\nAdding router {} to Network".format(len(matrix)+1))
	#showTopology(matrix) #print topology
	#Create Window to input matrix connection
	RtAdd = Tk()
	RtAdd.title("\nAdd Router {} to Network".format(len(matrix)+1))
	inputs = []
	Label(RtAdd, text="Destination Router").grid(row=0,column=0)
	Label(RtAdd, text="Cost").grid(row=0,column=1)
	Label(RtAdd, text="Enter '-1' for no Connection", bg='orange', relief=SUNKEN)
	for x in range(len(matrix)):
		Label(RtAdd, text=str(x+1), relief=RIDGE, width= 15).grid(row=x+2,column=0)
		e = Entry(RtAdd, relief=SUNKEN, width= 15)
		e.grid(row=x+2,column=1)
		e.insert(INSERT, '-1')
		inputs.append(e)
	#method stores rouer connections in array and closes input window
	data = []
	def on_press():
		for x in inputs:
			data.append(x.get())
		RtAdd.destroy()
	newRouter = Button(RtAdd, text="Done", width=15, command = on_press).grid(row=len(matrix)+2)
	#for a in inputs: a.pack()
	#newRouter.pack()
	RtAdd.mainloop()
	#add router to matrix and return to rebuild graph
	if '0' in data or not checkInputs(data):
		print("\nInputs can only contain Positive Integers or -1 for no connection!\nMatrix NOT Updated.")
		wait()
	#elif all("-1" == x for all x in data):
	#	print("New Node is\nMatrix NOT Updated.")
	#	wait()
	else:
		matrix.append(data) #add row to matric
		for x in range(len(matrix)-1): #add column to matrix
			matrix[x].append(data[x])
		matrix[-1].append('0')
		clearcmdline()
		print("(Added Router {}) ".format(len(matrix)), end = "")
		#showTopology(matrix) #print topology
		#wait()
	return

def routerDel(matrix):
	#get router input
	r = -1
	while not r in range(1, len(matrix)+1):
		clearcmdline()
		print("Select a router to delete between 1 and {}\nDelete Router: ".format(len(matrix)), end="", flush=True)
		r = input()
		if isInt(r): r = int(r)
		else: r = -1
	#modify matrix
	for x in range(len(matrix)):
		#print(matrix[x][r-1]) #delete column
		del matrix[x][r-1]
	del matrix[r-1] #delete row
	print("Deleted Router {}.\n".format(r))
	#print("(New) ", end = "")
	#showTopology(matrix) #print topology
	#wait()
	return

def Dijkstra(graph,s,d):
	G = nx.to_dict_of_dicts(graph)
	queuedNodes = G.keys()
	weights = {}
	prev = {}
	next_nodes = {}
	#inf weights between nodes to decrease and sort with no start nodes
	for n in G:
		weights[n] = float('inf')
		prev[n] = None
	scannedNodes = list() #set of all nodes scanned 
	weights[s] = 0 #start node has weight = 0
	#mainloop
	while len(scannedNodes) < len(queuedNodes): 
		l=[]
		#get list of all non-Scanned nodes and put in dict
		for n in queuedNodes:
			if n not in scannedNodes:
				l.append(n)
		toScan = {n: weights[n] for n in l}
		#get lightest node
		nextNode = min(toScan, key = weights.get)
		scannedNodes.append(nextNode) #put next node in scanned list
		neighbor = G[nextNode]
		for neighbors in neighbor: #scan neighbors to update dict of shortest paths
			 if weights[neighbors] > weights[nextNode] + G[nextNode][neighbors].get("weight"):
			 	weights[neighbors] = weights[nextNode] + G[nextNode][neighbors].get("weight")
			 	prev[neighbors] = nextNode
	#backtrack starting at dest using dict of previous nodes
	path = [d]
	while s not in path:
		path.append(prev[path[-1]]) #append previous node from last call in path until src
	path.reverse() #correct order of backtracked list
	length = weights[d] #last weights is path weight
	return (length, path)

def findAllPaths(graph,s,d): #find all paths with a depth up to matrix-1 number of nodes
	#find all paths without repeating nodes
	#trivial case (s=d) already handled in calling method
	maxDepth = len(matrix)
	G = nx.to_dict_of_dicts(graph)
	
	def getPathCost(p,G):
		pathWeight = 0
		for n in range(len(p)):
			if n+1 < len(p): 
				wt = G.get_edge_data(p[n], p[n+1])
				if wt: #handle nonexistent edge
					pathWeight += int(wt["weight"])
			else: #handle last call
				pass
		return pathWeight

	paths = []
	path = []
	visited = [False for x in range(maxDepth+1)]
	print("\nAll Paths Searched from Node {} to Node {}:\n".format(s,d), flush=True)


	def getAllPaths(src, dest, v, path, G): #Depth-First-Search
		v[src] = True
		path.append(src)
		#if d > maxDepth: return #redundant using vitisted array
		if src == dest:
			pWeight = getPathCost(path, G)
			displayPath(path, pWeight)
		else:
			for x in G[src]:
				if not v[x]:
					getAllPaths(x,dest,v,path, G)
		v[src] = False
		path.pop()

	#above function will generate a list of paths in 
	getAllPaths(s,d,visited,path,graph)	


def displayPath(path,weight):
	s = '->'.join(str(x) for x in path)
	s += ' Path Cost: {}'.format(weight)
	print(s)

def bestForBroadcast(G): #best weight determined by shortest cumulative distance to all other nodes
	weights = [float('inf')]*(len(G.nodes())+1)
	for x in G.nodes():
		wt = 0
		src = x
		for y in G.nodes():
			if y == x:
				pass
			elif not nx.has_path(G, src, y): #handle multiple disconnected networks
				pass
			else:
				(tmpWt, path) = Dijkstra(G,src, y)
				wt += tmpWt
		if wt > 0: #can only handle positive weights and not-isolated nodes
			weights[x] = wt #need to Normalize list
	bestWt = min(weights)
	return weights.index(bestWt)
########## MAIN METHOD ##########
clearcmdline()
matrix = None
#disp = None
#fwdtable = None
while True:
	done = False
	cmd = initialize()
	if not matrix:
		if not cmd == '1':
			clearcmdline()
			if cmd == "6":
				pass
			else:
				print("You must import a Network Topology (Option 1) before Proceeding!")
				cmd = 'invalid'
	if cmd == '1': 	#input network topology
		G=nx.Graph()
		G.clear()
		#ADD EDGES HERE
		ma = readMatrix(matrix)
		if ma: matrix = ma #only assign matrix if new file inputted
		buildGraph(G, matrix)
		#if disp: disp.exit()
		disp = _thread.start_new_thread(displayGraph, (G,))
		#forwardingTable = buildForwardingTable(matrix) #build list of all fwds from matrix
		#optimalpaths = bestPaths(forwardingTable) #build list of all optimal forwards from matrix (keeping redundancies)
		
		wait()
	elif cmd == '2': #generate forwarding table
		fwdtable = _thread.start_new_thread(showConnections,(matrix,)) #if fwdtable: fwdtable.exit()
	elif cmd == '3': #cmd == '3': #Paths from Source to Destination  (src, dest, matrix)
		done = False
		while not done: #src input
			clearcmdline()
			src = input("Select a source router between 1 and {}: ".format(len(matrix)))
			if isInt(src) and int(src) in range(1, len(matrix)+1):
				src = int(src)
				done = True
			else:
				print("\nInvalid input. Try again.\n")
		done = False
		while not done: #dest input
			dest = input("Select a destination router between 1 and {}: ".format(len(matrix)))
			#check input
			if isInt(dest) and int(dest)-1 in range(len(matrix)):
				dest = int(dest)
				done = True
				if dest == src: #trivial case
					print("\nSource and Destination router are the same.\nPath: {}->{}\nTotal cost: 0".format(src,dest))
					wait()
				else: #normal case
					if nx.has_path(G, src, dest): #check if path exists first by Dijsktra (Built into NetworkX already)
						(shortest_path_weight, shortest_path) = Dijkstra(G,src,dest)
						print(findAllPaths(G,src, dest))
						print("\nShortest Path:", flush=True)
						displayPath(shortest_path,shortest_path_weight)
						#print([p for p in nx.all_shortest_paths(G,src,dest)])
					else:
						print("There is no path from Node {} to Node {}.".format(src, dest))
					wait()
			else:
				clearcmdline()
				print("\nInvalid input. Try again.")
	elif cmd == '4': #Update Network Topology
		addDEL=None #reser val
		clearcmdline()
		print("(Old) ", end = "")
		showTopology(matrix) #print topology
		done = False
		while not done: #prompt for router add/del
			print("\nChoose an option:\n\t(1) Add a Router to Network Topology\n\t(2) Delete a Router from Network Topopogy\n\t(3) Cancel\n\nOption: ", end="", flush=True)
			addDEL = getCommandOption()
			if isInt(addDEL) and int(addDEL) in range(1,4):
				addDEL = int(addDEL)
				if addDEL == 1:
					done = True
					routerAdd(matrix) #if add (1) then call routeradd(matrix)
					G.clear()
					buildGraph(G, matrix) #show new graph
					_thread.start_new_thread(displayGraph, (G,))
					#print("(New) ", end = "")
					showTopology(matrix) #print topology
					wait()
				elif addDEL == 2:
					done = True
					routerDel(matrix) #if del (2) then call routerdel(router)
					G.clear()
					buildGraph(G, matrix) #show new graph
					_thread.start_new_thread(displayGraph, (G,))
					print("(New) ", end = "")
					showTopology(matrix) #print topology
					wait()
				elif addDEL == 3:
					done = True
					print("Selection Canceled.") #input 
			else: 
				print("Bad input. Try again.")
		#forwardingTable = buildForwardingTable(matrix)
		#optimalpaths = bestPaths(forwardingTable)
	elif cmd == '5': #Best Router for Broadcast
		clearcmdline()
		bestNode = bestForBroadcast(G)
		print("Best Node for Broadcast: Node {}".format(bestNode), flush=True)
		#find router with shortest path to other routers w/cost of each path
		#call (3) for every router on every router
		pass
	elif cmd == '6': #Exit
		clearcmdline()
		print("Exit CS542 2019 Spring project. Good Bye!")
		exit(0)
	else:
		print("\nInvalid Command. Try again.")
		wait()

