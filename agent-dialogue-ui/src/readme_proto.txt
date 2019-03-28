How to generate protobuf files:



If the compiler complains, you need to modify the generated .js files by hand:

If you see "'proto' is not defined"

after
  // GENERATED CODE -- DO NOT EDIT!
add
  /* eslint-disable */


If you see "Cannot compile namespaces when the '--isolatedModules' flag is provided"

after
  // GENERATED CODE -- DO NOT EDIT!
add
  export const _ = '';
