#!/usr/bin/python

import sys
import os
import shutil

isClass = False
icon_2 = ""
if sys.argv[0].find("makeIcons.py") == -1:
    isClass = True
if isClass == False:
    if len(sys.argv) < 3:
        print """Format: parameters:
        1. type (icon_1,icon_2)
        2. path to icon
        4. destination dir
        5. path from icon"""
        sys.exit(1)
    type = sys.argv[1]
    icon =  sys.argv[2]
    pwd = sys.argv[3]
if len(sys.argv) > 4:
    icon_2 =  sys.argv[4]


def createImg(int_path_img,out_path_img,resolution,extend_param):
    cmd = "convert_img " + int_path_img + " -resize " + resolution + " " + extend_param + " -format PNG32 " + out_path_img
    os.system(cmd)


class CreaterIcons:

    def __init__(self):
        self.resolutions={}
        self.params=" -interpolate Nearest -filter point -antialias "
        self.showOnlyEr = False
        
    def showOnlyError(self):
        self.showOnlyEr = True
        return
        
    def create(self,type,icon,icon2,pwd):
        resolutions_2={}
        path_to_res = pwd + "/res"
        if os.path.exists(path_to_res) == False :
            os.makedirs(path_to_res)
        resolutions={}
        out_name = "icon.png"
        if type == "icon_1" :
            resolutions={"drawable":"96x96",
                    "drawable-hdpi":"72x72",
                    "drawable-ldpi":"36x36",
                    "drawable-mdpi":"48x48",
                    "drawable-xhdpi":"96x96",
                    "drawable-xxhdpi":"144x144",
                    "drawable-xxxhdpi":"192x192" }    
        elif type == "icon_2" :
            out_name = "moto_crash.png"
            resolutions={
                    "drawable-hdpi":"48x48",
                    "drawable-ldpi":"36x36",
                    "drawable-mdpi":"32x32",
                    "drawable-xhdpi":"64x64",
                    "drawable-xxhdpi":"96x96" }
        for name in resolutions:
            resolution = resolutions[name]
            if self.showOnlyEr == False:
                print (name +" - " + resolution)
            path_to_icon = path_to_res + "/" + name
            if os.path.exists(path_to_icon) == False :
                os.makedirs(path_to_icon)
            createImg(icon, path_to_icon + "/" + out_name, resolution, self.params)
            #createImg(icon, path_to_icon + "/icon.png", resolution, self.params)
            #cmd = "convert_img " + icon + " -resize " + resolution + self.params +  " -format PNG32 " + path_to_icon + "/icon.png"
            #os.system(cmd)


#######################################


if isClass == False:
    CreaterIcons().create(type,icon,icon_2,pwd)
    sys.exit(0)

