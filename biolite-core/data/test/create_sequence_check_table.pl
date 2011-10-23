use Bio::SeqIO;
 use Digest::MD5 qw(md5_hex);

$in  = Bio::SeqIO->new(-file => $ARGV[0] , '-format' => 'Fasta');

while ( my $seq = $in->next_seq() ) {
    print $seq->primary_id,"\t";
    print $seq->description,"\t";
    print md5_hex($seq->seq),"\n";
}
