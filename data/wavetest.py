#!/usr/bin/env python
import sys
from pylab import *
import wave
from PIL import Image
import json
import contextlib

output = {}

output['timecodes'] = []

def show_wave_n_spec(speech):
    spf = wave.open(speech, 'r')
    sound_info = spf.readframes(-1)
    sound_info = fromstring(sound_info, 'Int16')

    f = spf.getframerate()

    subplot(211)
    plot(sound_info)
    
    savefig('foo.png')
    spf.close()

fil = raw_input('Name of file (or path if not in same dir): ')
show_wave_n_spec(fil)


with contextlib.closing(wave.open(fil,'r')) as f:
    frames = f.getnframes()
    rate = f.getframerate()
    duration = frames / float(rate)
    print duration

im = Image.open("foo.png")
pixels = im.load()

xstart = 110
xend = 710
length = duration*30
xdiff = (xend-xstart)/length

x = 1
maxx = 0
summ = 0
for i in range(xstart,xend): # for every pixel:
    for j in range(77,277):
        each = {}
        each['time'] = str((float(i-xstart)/float(600))*length)
        if pixels[i,j][0] != 255:
            sick = False
            amp = 0
            while not sick:
                j += 1
                if pixels[i,j][0] == 255:
                    break
                amp += 1
                pixels[i,j] = (0,0,0)
            if amp > maxx:
                maxx = amp
            summ += amp
            each['amplitude'] = amp
            output['timecodes'].append(each)
            break

average = summ/length

def normalize(inputx,maxx,average):
    avgnorm = average/maxx
    for each in inputx['timecodes']:
        time = float(each['amplitude'])/float(maxx)
        if time > avgnorm:
            each['amplitude'] = float(float(each['amplitude'])/float(maxx))**0.6
        else:
            each['amplitude'] = float(float(each['amplitude'])/float(maxx))**(1.4)

normalize(output,maxx,average)

with open('data.json', 'w') as outfile:
    json.dump(output, outfile)

