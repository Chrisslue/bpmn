# Install LoLA (Windows)

## Using the pre-compiled binary

Copy the LoLA binary along with the required Cygwin DLLs from this directory

* `lola.exe`
* `cyggcc_s-seh-1.dll`
* `cygstdc++-6.dll`
* `cygwin1.dll`

to a directory of coice, e. g. `C:\Tools`.

Finally, **add the directory `C:\Tools` to the `PATH` environment variable.**

LoLA is now globally installed.

## From source

As LoLA is implemented against the POSIX API, direct compilation on Windows systems is not possible. Therefore [Cygwin](https://cygwin.com), a POSIX-compatible environment that runs natively on Windows, must be used to compile LoLA for Windows.

Cygwin allows programs of Unix-like systems to be compiled and run natively on Windows by providing them with the same underlying POSIX API they would expect in those systems.

### Install Cygwin

Download the Cygwin (64-bit) installer `setup-x86_64.exe` from the [official website](https://cygwin.com) (direct download from [here](https://cygwin.com/setup-x86_64.exe)).

Install Cygwin. You do not need to change any default values. When asked to install additional packages, make sure to select:

* `gcc-g++`
* `make`

*Hint: If you don't see the packages, make sure the "View" dropdown selection is set to `Full`.*

Finally, **add Cygwin to the `PATH` environment variable**. The default installation destination for Cygwin is `C:\cygwin64`, so you will need to `C:\cygwin64` to the PATH variable.

### Build LoLA

Download the latest distribution of LoLA from the  [official website](http://service-technology.org/lola/) (direct download from [here](http://service-technology.org/files/lola/lola-2.0.tar.gz)).

Go to your download directory and extract the donwloaded tar file `lola-2.0.tar.gz` using an application like [7-Zip](https://www.7-zip.org).

Alternatively, you can use the `Cygwin64 Terminal` application which has been installed along with Cygwin. Open `Cygwin64 Terminal` and execute

```bash
  cd c:
  cd Users/<Username>/Downloads
  tar -xzf lola-2.0.tar.gz
```

This creates a directory `lola-2.0` which contains the LoLA distribution.


**Important: The source file `lola-2.0\src\ThirdParty\minisat\utils\System.cc` contains a bug which must be corrected prior to compilation (the bug does only affect Windows systems).**

Open the file `lola-2.0\src\ThirdParty\minisat\utils\System.cc`. At the end of the file (line 93), change

```c++
#else
double Minisat::memUsed() {
    return 0; }
#endif
```

to

```c++
#else
double Minisat::memUsed()     { return 0; }
double Minisat::memUsedPeak() { return 0; }
#endif
```

and save the file.

After this, proceed to the actual compilation of LoLA. Open the `Cygwin64 Terminal` application and navigate to the LoLA distribution

```bash
  cd c:
  cd Users/<Username>/Downloads/lola-2.0
```

Now configure LoLA by executing

```bash
  cd lola-2.0
  ./configure
```

Configuration should finish with a success message like

```text
============================================================
  Successfully configured LoLA 2.0.
  -> compile LoLA with ‘make’.
============================================================
```

Then, execute

```bash
  make
```

to compile LoLA. You may ignore potential compiler warnings. Again, you should see a success message like

```text
============================================================
  Successfully compiled LoLA 2.0.
  -> check out LoLA’s help with ‘src/lola --help’
  -> install LoLA to /usr/local/bin with ‘make install’
============================================================
```

This inidicates that the LoLA binary `lola.exe` has been successfully built in the `src` directory.

Finally, copy the LoLa binary to a directory of choice, e. g. `C:\Tools` and **add the directory to the `PATH` environment variable.**

LoLA is now globally installed.

The directory `lola-2.0` is not needed any longer. You can safely delete it.
