module %s;
fn int add(int a, int b) @export("add_something")
{
    return a + b;
}
fn int sub(int a, int b) @export("sub_something")
{
    return a - b;
}
