# Readme

Welcome to the Cash in Emergencies app source code.

The app is designed to be a tool for ARC to distribute cash during emergency situations. The requirement for distributing cash is to follow certain guidelines outlined in documents served by a Document Management System.

## Usage

Import the project into Android Studio by importing the `settings.gradle` file.

The main entry point to the application is the [MainApplication.kt](app/src/main/java/com/cube/arc/cie/MainApplication.kt) singleton. Here the analytics helper and the `DirectoryManager` class is initialised.

`DirectoryManager` is the main 'brain' of the application, this deals with loading the app content structure into models and provides various convinience methods (as part of the DMSDK). Extension method/properties can be found in [lib/util/](app/src/main/java/com/cube/lib/util/)

The `workflow` section of the app is the main feature, which displays a multi-nested structure of `Directory`s with related documents attached to those directories. These contain the tools and documents necessary for (xxx) to follow the procedure and protocol to distribute cash in an emergency.

Each feature of the app is split into its own package with a standard sub-package structure with a corresponding `res` folder.

For example, the `workflow` feature has the following sub packages

- workflow/activity

This package hosts all the activities that are used in or as part of the workflow feature

- workflow/adapter

This package hosts all recycler adapters

- workflow/fragment

This package hosts all the fragments

- workflow/manager

This package hosts all the manager singletons

- workflow/model

This package hosts all the data models

- workflow/view

This package hosts all the view holders used in the recycler adapter

- res-screen/workflow/

This resource package hosts all views that are used within the workflow feature only. Any other view/resource that is used app-wide is defined in the default `res-main` folder

The only exception is the `com/cube/arc/cie` package. This is the 'main' entry to the app and hosts any source file that is used app-wide or is a small sub-feature that doesnt warrant it's own sub-package.

## Pre-requisites

This project requires the [DMSDK]() to function, this can be located in the `sdk/dmsdk/` folder.

Also required is Android Studio 3, Kotlin lang version `1.1.3-2`

The app is built with android API `17` as minSDK and currently only supports up to API `25`

## Build values

Build values are required for the app to work correctly, see [example.properties](app/example.properties) for all values

## License

BSD 3-Clause License
Copyright (c) 2017, 3 SIDED CUBE
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
* Neither the name of the copyright holder nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
