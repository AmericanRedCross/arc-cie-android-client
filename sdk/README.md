# Readme

This is the DMSDK for the [DMS]() system created to manage documents in a hierarchical way.

# Usage

Either download this SDK and include it into your project, or you can copy the aar directly from [here (v1.0)]()

The library is made up of 2 parts, the `DirectoryManager` singleton, and the data structures.

The `DirectoryManager` class is the main source used to initialise a structure json file into workable models and hosts a few convenience methods.

Use one of the 4 `init` methods within `DirectoryManager` to load a structure ([example](sdk/dmsdk/src/test/resources/structure.json)). You will then be able to access directories via `DirectoryManager.directories` or the `DirectoryManager.directory(id)` methods

## Pre-requisites

Android Studio 3 (version RC1 or newer), Kotlin lang version `1.1.3-2`

The SDK is built with android API `17` as minSDK and currently only supports up to API `25`

## TODO

Currently in development on `feature/content-manager` is an additional manager class to be used for downloading standard files and bundles from the DMS system.

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
