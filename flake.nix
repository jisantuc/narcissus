{
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-23.05";

  outputs = { self, nixpkgs }:
    let
      # these probably aren't all necessary, but they're good smoke for when I run into
      # features not supported on specific architectures before someone tells me they got
      # burned by it
      supportedSystems = [ "x86_64-linux" "x86_64-darwin" "aarch64-linux" "aarch64-darwin" ];
      forAllSystems = nixpkgs.lib.genAttrs supportedSystems;
      pkgs = forAllSystems (system: nixpkgs.legacyPackages.${system});
    in
    {
      devShells = forAllSystems (system: {
        default = pkgs.${system}.mkShell {
          packages =
            let
              development = (p: [
                p.nodejs-18_x
                p.sbt
                p.yarn
              ]);
              infra = (p: [
                p.git
                p.git-crypt
                p.terraform
                p.terraform-ls
              ]);
              docs = (p: [
                p.adrgen
              ]);
              applyToSystemPackages = (f: f pkgs.${system});
            in
            builtins.map applyToSystemPackages [ development infra docs ];
        };
      });
    };
}
