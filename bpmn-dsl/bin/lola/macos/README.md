# Install LoLA (macOS)

## Using the pre-compiled binary

Copy the LoLA binary `lola` from this directory to `/usr/local/bin/`.

Done.

## From source

### Prerequisites

* Working C++ compiler such GCC or Clang

### Build LoLA

Download the latest distribution of LoLA from the [official website](http://service-technology.org/lola/) (direct download form [here](http://service-technology.org/files/lola/lola-2.0.tar.gz)).

Go to your download directory and extract the downloaded tar file `lola-2.0.tar.gz` by executing

```bash
  tar -xzf lola-2.0.tar.gz
```

This creates a directory `lola-2.0` which contains the LoLA distribution. First, you need to configure LoLA by executing

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

This indicates that the LoLA binary `lola` has been successfully built in the `src` directory.

Finally, execute

```bash
  make install
```

This will copy the LoLA binary to `/usr/local/bin/`.

LoLA is now globally installed.

The directory `lola-2.0` is not needed any longer. You can safely delete it.
