## BundleArgs

Annotation processor to generate type-safe strongly-typed Builder classes around weakly-typed Intent/Bundle API.

### Example

```java
@BundleBuilder
public class MyActivity extends AppCompatActivity
{
    @Arg
    String someStringArgument;
    
    @Arg
    int someIntArgument;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        BundleArgs.inject(getIntent().getExtras(), this); //you can move this into BaseActivity
    }
}
```

```java
@BundleBuilder
public class MyFragment extends Fragment
{
    @Arg(optional=true) //you can mark arguments as optional, by default all arguments are mandatory
    SomeModel someSerializableArgument; //you can pass serializable POJOs
    
    //on kotlin, you can use it like this:
    //@Arg(optional=true)
    //lateinit var someSerializableArgument : SomeModel
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getLayout(), container, false);
        BundleArgs.inject(getArguments(), this); //you can move this into BaseFragment
        return view;
    }
}
```

After you hit build, two classes will be generated: ``MyActivityBundleBuilder`` and ``MyFragmentBundleBuilder``. You can use them as such:

```java
new MyActivityBundleBuilder()
    .someStringArgument("someValue")
    .someIntArgument(5)
    .startActivity(context);
//instead of startActivity, you can also use:
//.build() -> will return a Bundle object
//.buildIntent(context) -> will return an Intent object
```

```java
MyFragment fragment = new MyFragmentBundleBuilder()
     .someSerializableArgument(someObject)
     .createFragment();
//instead of createFragment, you can also use
//.build() -> will return a Bundle object
```

### Why?

- Because plain Intents and Bundles are a pain to work it. So much verbosity to do such an easy task, passing arguments from one object to another.
- Because by using plain Bundle/Intent API, you loose Java/Kotlin greatest power: TYPE SAFETY.
- Because some of us work on legacy projects and cannot migrate to Google's Jetpack single-activity, fragment + SafeArgs based navigation architecture.

### Import

```
repositories {
    maven { url "https://andob.io/repository/open_source" }
}
```

```
dependencies {
    implementation 'ro.andob.bundleargs:bundleargs-annotation:2.2.0'
    annotationProcessor 'ro.andob.bundleargs:bundleargs-processor:2.2.0'
}
```

### This is a fork, you can find the original library [here](https://github.com/MFlisar/BundleArgs)

### License

```
Copyright 2015 Emil Sjölander, 2018-2024 Dobrescu Andrei

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
