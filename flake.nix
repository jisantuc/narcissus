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
              systemPackages = pkgs.${system};
              development = (p: [
                p.sbt
                p.yarn
                p.nodejs-18_x
              ]);
              infra = (p: [
                p.terraform
              ]);
              docs = (p: [
                p.adrgen
              ]);
              applyToSystemPackages = (f: f systemPackages);
            in
            builtins.map applyToSystemPackages [ development infra docs ];
        };
      });
    };
}