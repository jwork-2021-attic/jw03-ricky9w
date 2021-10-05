# W03

191220119 王毓琦

## 文件结构

本次作业文件数量较少, 结构稍乱:

+ `example/RickyBubbleSorter.java` 和 `example/RickySelectSorter.java` 是两种算法的源文件, 为了方便起见直接放在 `example/` 目录里. 实际运行文件时为了让代码能够执行到预期流程是没有这两个文件的.
+ `example/resources` 里新增的两张图片是网上找到的载体原图
+ `example.*.png`  是对应算法生成的成品图片
+ `result-*.txt` 是算法运行结果图片

## 示例代码解读

从隐写术图中读取和加载类的关键在 `example/classloader/SteganographyClassLoader` 中对 ClassLoader 的重载, 而示例代码中从给定图片加载对应类的关键在这里:

```java
SteganographyClassLoader loader = new SteganographyClassLoader(
    new URL("https://raw.githubusercontent.com/jwork-2021/jw03-ricky9w/main/example.RickySelectSorter.png"));

Class c = loader.loadClass("example.RickySelectSorter");

Sorter sorter = (Sorter) c.newInstance();
```

可以单步调试查看类加载时究竟发生了什么.

在执行这一行代码:

```java
Class c = loader.loadClass("example.RickySelectSorter");
```

由于 `example.SteganographyClassLoader` 类并没有对 `loadClass` 方法进行重写, 因此这一步调用的是默认的 `loadClass` 方法:

```java
public Class<?> loadClass(String name) throws ClassNotFoundException {
    return loadClass(name, false);
}
```

 在这一步使用参数 `resolve=false` 调用了 `loadClass` 方法:

```java
protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
```

这里首先调用 `findLoadedClass()` 方法检查该类是否已经被加载, 如果没有加载则会调用 `parent.ClassLoader()` 进行加载. 如果此时对应源文件在项目中的话这一步就可以成功加载类, 而我们已经删除了对应文件, 因此无法无法成功加载, 来到下一步:

```java
if (c == null) {
    // If still not found, then invoke findClass in order
    // to find the class.
    long t1 = System.nanoTime();
    c = findClass(name);
	...
}
```

 在这里将会调用 `findClass()` 方法, 实际调用的是重写过的方法:

```java
@Override
protected Class<?> findClass(String name) throws ClassNotFoundException {

    try {
        BufferedImage img = ImageIO.read(url);

        SteganographyEncoder encoder = new SteganographyEncoder(img);
        byte[] bytes = encoder.decodeByteArray();
        return this.defineClass(name, bytes, 0, bytes.length);
    } catch (Exception e) {
        throw new ClassNotFoundException();
    }

}
```

该方法会从给定 URL 加载图片, 并将其中隐写的类的字节码解析并放在 `bytes[]` 数组. 最后调用 `defineClass()` 方法生成最终的类, 加载成功.

最后在 `example.Scene` 当中调用 `c.newInstance()` 并加上强制类型转换生成加载到的 `Sorter` 类的实例.

## 图片展示

BubbleSorter:

![](https://raw.githubusercontent.com/jwork-2021/jw03-ricky9w/main/example.RickyBubbleSorter.png)

SelectSorter:

![](https://raw.githubusercontent.com/jwork-2021/jw03-ricky9w/main/example.RickySelectSorter.png)

## 结果展示

### 自己的Sorter运行结果

BubbleSorter运行结果如下:

[![asciicast](https://asciinema.org/a/2mpDOcQKcOHHiVzw83pCQlylX.svg)](https://asciinema.org/a/2mpDOcQKcOHHiVzw83pCQlylX)

SelectSorter运行结果如下:

[![asciicast](https://asciinema.org/a/hLgwiIg8dUhy5LMI42vV5eh9B.svg)](https://asciinema.org/a/hLgwiIg8dUhy5LMI42vV5eh9B)

### 同学的排序算法运行结果

使用了[马润泽](https://github.com/jwork-2021/jw03-Mars-Z777)的图片.

同学的[选择排序算法](https://raw.githubusercontent.com/jwork-2021/jw03-Mars-Z777/main/example.ChooseSorter.png)结果如下:

[![asciicast](https://asciinema.org/a/uRjvFH8zCT8Rmonh5YJJMoLkA.svg)](https://asciinema.org/a/uRjvFH8zCT8Rmonh5YJJMoLkA)

同学的[快速排序算法](https://raw.githubusercontent.com/jwork-2021/jw03-Mars-Z777/main/example.QuickSorter.png)结果如下:

[![asciicast](https://asciinema.org/a/lXYDgFxof3lYzoU2bHdGpiUja.svg)](https://asciinema.org/a/lXYDgFxof3lYzoU2bHdGpiUja)

