import networkx as nx
import matplotlib as mpl
import matplotlib.pyplot as plt
import numpy as np
import sys


def plotGraph(N, I, pn, T, M, q, F, start=0):
    my_path = 'N{}I{}pn{}T{}M{}q{}F{}'.format(N, I, pn, T, M, q, F, '/', start)
    G = nx.read_edgelist('../Outputs/{}/{}.csv'.format(my_path, start),
                         nodetype=int,
                         data=(('weight', float), ),
                         create_using=nx.Graph())
    edges = G.edges()
    pos = nx.circular_layout(G)
    colors = [G[u][v]['weight'] for u, v in edges]
    new_colors = []
    for el in colors:
        new_colors.append(el * 255 / 3)
    nx.draw(G, pos, node_size=700 / int(N), node_color='b')
    nx.draw_networkx_edges(G,
                           pos,
                           edgelist=edges,
                           width=4,
                           edge_color=new_colors,
                           edge_cmap=plt.cm.Blues)
    nx.draw_networkx_edges(G,
                           pos,
                           edgelist=edges,
                           width=0.5,
                           edge_color='gray',
                           alpha=1)
    plt.title('N={} I={} pn={} T={} M={} q={} F={}'.format(
        N, I, pn, T, M, q, F))
    plt.savefig('Graphs/N{}I{}pn{}T{}M{}q{}F{}_{}.pdf'.format(
        N, I, pn, T, M, q, F, start),
                format="PDF")


N = sys.argv[1]
I = sys.argv[2]
pn = sys.argv[3]
T = sys.argv[4]
M = sys.argv[5]
q = sys.argv[6]
F = sys.argv[7]
start = sys.argv[8]
plotGraph(N, I, pn, T, M, q, F, start)
