import java.io.FilterOutputStream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        IndexableSkipList s = new IndexableSkipList(0.25);
        s.insert(12);
        s.insert(17);
        s.insert(20);
        s.insert(25);
        s.insert(31);
        s.insert(38);
        s.insert(40);
        s.insert(44);
        s.insert(50);
        s.insert(55);

        System.out.println(s);

        AbstractSkipList.SkipListNode _31 = s.find(31);
        AbstractSkipList.SkipListNode _28 = s.find(28);
        AbstractSkipList.SkipListNode _56 = s.find(56);
        System.out.println(_31);
        System.out.println(_28);
        System.out.println(_56);

        s.delete(_31);
        s.delete(_56);
    }
}