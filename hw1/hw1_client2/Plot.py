#!/usr/bin/env python
# coding: utf-8

import csv
import matplotlib.pyplot as plt

def plot_throughput(file_path):
    times = []
    with open(file_path, 'r') as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:
            if(row[0] == "start_time"):
                continue
            times.append(float(row[0])/1000)

    start_time = min(times)
    end_time = max(times)
    x_range = range(0, int(end_time)- int(start_time) +1)
    
    y_values = [0] * len(x_range)
    for time in times:
        y_values[int(time-start_time)] += 1

    plt.plot(x_range, y_values)
    plt.xlabel('Time (s)')
    plt.ylabel('Throughput (requests/s)')
    plt.show()

plot_throughput('./record.csv')





