import pandas as pd
import matplotlib.pyplot as plt
import os
import sys


def plot_convergence(col_name, fixed):
    my_col = pd.read_csv("output.csv")
    string = ""
    for key, value in fixed.items():
        string = string + key + value
        my_col = my_col.loc[my_col[key] == float(value)]
        my_col = my_col.drop([key], axis=1)
    if my_col.empty:
        print("Empty")
    else:
        my_col.plot(x=col_name, y=["steps"], kind="bar", rot=0)
        dir_name = "Barcharts/{}".format(col_name)
        if not os.path.exists(dir_name):
            os.mkdir(dir_name)
        plt.savefig("{}/{}.png".format(dir_name, string))


my_dict = {1: 'N', 2: 'I', 3: 'pn', 4: 'T', 5: 'q', 6: 'F'}
fixed = {}
for i in range(1, 7):
    if sys.argv[i] == '?':
        x = my_dict[i]
    else:
        fixed[my_dict[i]] = sys.argv[i]
plot_convergence(x, fixed)
